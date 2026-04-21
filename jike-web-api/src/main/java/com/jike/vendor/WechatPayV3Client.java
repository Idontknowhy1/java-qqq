package com.jike.vendor;

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 微信支付 v3 API 客户端（使用 AES-256-GCM 获取平台证书，不依赖 RSA 平台证书配置）
 * 参考 server 项目的实现方案
 */
@Slf4j
@Component
public class WechatPayV3Client {

    private static final String WECHAT_API_BASE = "https://api.mch.weixin.qq.com";

    @Autowired
    private WeiXinConfig weixinConfig;

    // 平台证书缓存
    private final Map<String, PlatformCert> platformCertCache = new ConcurrentHashMap<>();
    private long lastCertFetchMs = 0;
    private static final long CERT_REFRESH_INTERVAL_MS = 5 * 60 * 1000; // 5分钟

    @PostConstruct
    public void init() {
        log.info("WechatPayV3Client 初始化完成");
    }

    /**
     * 创建 Native 扫码支付订单
     */
    public NativeOrderResult createNativeOrder(String description, int amountFen, String outTradeNo, String attach) {
        NativeOrderResult result = new NativeOrderResult();
        try {
            String urlPath = "/v3/pay/transactions/native";

            // 使用 FastJSON 构建请求体，避免 JSON 字符串拼接转义问题
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("appid", weixinConfig.getOfficeAppId());
            bodyJson.put("mchid", weixinConfig.getMchId());
            bodyJson.put("description", description);
            bodyJson.put("out_trade_no", outTradeNo);
            bodyJson.put("notify_url", weixinConfig.getNotifyUrl());

            JSONObject amountJson = new JSONObject();
            amountJson.put("total", amountFen);
            amountJson.put("currency", "CNY");
            bodyJson.put("amount", amountJson);

            if (attach != null && !attach.isEmpty()) {
                bodyJson.put("attach", attach);
            }

            String body = bodyJson.toJSONString();
            log.info("创建Native订单请求体: {}", body);

            String response = request("POST", urlPath, body);

            // 解析 code_url
            if (response.contains("\"code_url\"")) {
                result.setSuccess(true);
                // 简单提取 code_url
                int start = response.indexOf("\"code_url\"") + 12;
                int end = response.indexOf("\"", start);
                result.setCodeUrl(response.substring(start, end));
            } else {
                result.setSuccess(false);
                result.setError("创建订单失败: " + response);
            }
        } catch (Exception e) {
            log.error("创建微信Native订单失败", e);
            result.setSuccess(false);
            result.setError(e.getMessage());
        }
        return result;
    }

    /**
     * 查询订单支付状态
     */
    public QueryOrderResult queryOrderByOutTradeNo(String outTradeNo) {
        QueryOrderResult result = new QueryOrderResult();
        try {
            String urlPath = String.format("/v3/pay/transactions/out-trade-no/%s?mchid=%s",
                java.net.URLEncoder.encode(outTradeNo, StandardCharsets.UTF_8),
                java.net.URLEncoder.encode(weixinConfig.getMchId(), StandardCharsets.UTF_8));

            String response = request("GET", urlPath, null);
            // 解析 trade_state
            if (response.contains("\"trade_state\"")) {
                int start = response.indexOf("\"trade_state\"") + 14;
                int end = response.indexOf("\"", start);
                result.setTradeState(response.substring(start, end));
            }
        } catch (Exception e) {
            log.error("查询微信订单失败", e);
        }
        return result;
    }

    /**
     * 发送请求到微信支付 API
     */
    private String request(String method, String urlPath, String body) throws Exception {
        String url = WECHAT_API_BASE + (urlPath.startsWith("/") ? "" : "/") + urlPath;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = generateNonceStr(16);
        String authorization = buildAuthorization(method, urlPath, body, timestamp, nonceStr);

        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", authorization);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        if (body != null && !body.isEmpty()) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode >= 400) {
            throw new Exception(String.format("微信支付请求失败(%d): %s", responseCode, response));
        }

        return response.toString();
    }

    /**
     * 构建微信支付 v3 签名
     */
    private String buildAuthorization(String method, String urlPath, String body, String timestamp, String nonceStr) {
        try {
            String canonicalUrl = urlPath.startsWith("/") ? urlPath : "/" + urlPath;
            String message = String.format("%s\n%s\n%s\n%s\n%s\n",
                method.toUpperCase(),
                canonicalUrl,
                timestamp,
                nonceStr,
                body != null ? body : "");

            String signature = signMessage(message);

            return String.format("WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",timestamp=\"%s\",serial_no=\"%s\",signature=\"%s\"",
                weixinConfig.getMchId(),
                nonceStr,
                timestamp,
                weixinConfig.getMchSerialNo(),
                signature);
        } catch (Exception e) {
            log.error("构建签名失败", e);
            throw new RuntimeException("构建签名失败", e);
        }
    }

    /**
     * 使用商户私钥签名
     */
    private String signMessage(String message) throws Exception {
        File keyFile = new File(weixinConfig.getPrivateKeyPath());
        if (!keyFile.exists()) {
            throw new Exception("商户私钥文件不存在: " + weixinConfig.getPrivateKeyPath());
        }

        // 读取 PEM 文件内容
        String privateKeyPem;
        try (BufferedReader reader = new BufferedReader(new FileReader(keyFile).getReader())) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            privateKeyPem = sb.toString();
        }

        // 移除 PEM 头尾
        privateKeyPem = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        java.security.PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 生成随机字符串
     */
    private String generateNonceStr(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 获取平台证书（使用 AES-256-GCM 解密，不依赖 RSA 配置）
     */
    private synchronized void refreshPlatformCertificatesIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCertFetchMs < CERT_REFRESH_INTERVAL_MS && !platformCertCache.isEmpty()) {
            return;
        }

        lastCertFetchMs = now;
        log.info("刷新微信平台证书...");

        try {
            String response = request("GET", "/v3/certificates", null);
            platformCertCache.clear();

            // 解析证书列表
            // 格式: {"data":[{"serial_no":"xxx","effective_time":"xxx","expire_time":"xxx","encrypt_certificate":{"algorithm":"AEAD_AES_256_GCM","nonce":"xxx","associated_data":"xxx","ciphertext":"xxx"}}]}
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSON.parseObject(response);
            com.alibaba.fastjson.JSONArray data = json.getJSONArray("data");

            for (int i = 0; i < data.size(); i++) {
                com.alibaba.fastjson.JSONObject item = data.getJSONObject(i);
                String serialNo = item.getString("serial_no");
                String expireTime = item.getString("expire_time");

                com.alibaba.fastjson.JSONObject encryptCert = item.getJSONObject("encrypt_certificate");
                String ciphertext = encryptCert.getString("ciphertext");
                String nonce = encryptCert.getString("nonce");
                String associatedData = encryptCert.getString("associated_data");

                // 解密获取公钥
                String publicKeyPem = decryptAes256GcmBase64(weixinConfig.getApiV3Key(), associatedData, nonce, ciphertext);

                PlatformCert cert = new PlatformCert();
                cert.setSerial(serialNo);
                cert.setPublicKeyPem(publicKeyPem);
                cert.setExpiresAtMs(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(expireTime).getTime());

                platformCertCache.put(serialNo, cert);
                log.info("缓存平台证书: serial={}, expire={}", serialNo, expireTime);
            }

            log.info("平台证书刷新完成，共 {} 个", platformCertCache.size());
        } catch (Exception e) {
            log.error("刷新平台证书失败", e);
        }
    }

    /**
     * AES-256-GCM 解密
     */
    private String decryptAes256GcmBase64(String apiV3Key, String associatedData, String nonce, String ciphertextBase64) throws Exception {
        byte[] apiV3KeyBytes = apiV3Key.getBytes(StandardCharsets.UTF_8);
        byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
        byte[] associatedDataBytes = associatedData.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertextBase64);

        // 前 16 字节是 authTag
        byte[] authTag = new byte[16];
        byte[] data = new byte[ciphertextBytes.length - 16];
        System.arraycopy(ciphertextBytes, ciphertextBytes.length - 16, authTag, 0, 16);
        System.arraycopy(ciphertextBytes, 0, data, 0, ciphertextBytes.length - 16);

        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
        javax.crypto.spec.GCMParameterSpec spec = new javax.crypto.spec.GCMParameterSpec(128, nonceBytes);
        javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(apiV3KeyBytes, "AES");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec, spec);
        cipher.updateAAD(associatedDataBytes);

        byte[] decrypted = cipher.doFinal(data);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 验证微信回调签名
     */
    public boolean verifyNotifySignature(String timestamp, String nonce, String body, String signature, String serialNo) {
        try {
            refreshPlatformCertificatesIfNeeded();
            PlatformCert cert = platformCertCache.get(serialNo);
            if (cert == null) {
                log.error("未找到对应 serial 的平台证书: {}", serialNo);
                return false;
            }

            String message = timestamp + "\n" + nonce + "\n" + body + "\n";
            return verifySignature(cert.getPublicKeyPem(), message, signature);
        } catch (Exception e) {
            log.error("验签失败", e);
            return false;
        }
    }

    /**
     * 验证签名
     */
    private boolean verifySignature(String publicKeyPem, String message, String signatureBase64) throws Exception {
        // 移除 PEM 头尾
        publicKeyPem = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        java.security.PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(message.getBytes(StandardCharsets.UTF_8));

        return verifier.verify(Base64.getDecoder().decode(signatureBase64));
    }

    /**
     * 解密回调通知中的资源
     */
    public String decryptNotifyResource(String algorithm, String ciphertextBase64, String nonce, String associatedData) {
        try {
            return decryptAes256GcmBase64(weixinConfig.getApiV3Key(), associatedData, nonce, ciphertextBase64);
        } catch (Exception e) {
            log.error("解密回调资源失败", e);
            throw new RuntimeException("解密回调资源失败", e);
        }
    }

    // ==================== 内部类 ====================

    @Getter
    @Setter
    public static class NativeOrderResult {
        private boolean success;
        private String codeUrl;
        private String error;
    }

    @Getter
    @Setter
    public static class QueryOrderResult {
        private String tradeState;
        private String error;
    }

    @Getter
    @Setter
    private static class PlatformCert {
        private String serial;
        private String publicKeyPem;
        private long expiresAtMs;
    }
}
