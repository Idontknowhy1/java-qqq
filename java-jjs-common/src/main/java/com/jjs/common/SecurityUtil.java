package com.jjs.common;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES的加密和解密
 * @author libo
 */
public class SecurityUtil {

    public static class aes {
        //密钥 (需要前端和后端保持一致)
//    private static final String KEY = "46EBA22EF5204DD5B110A1F730513965";
        //算法
        private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

        /**
         * 将byte[]转为各种进制的字符串
         * @param bytes byte[]
         * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
         * @return 转换后的字符串
         */
        private static String binary(byte[] bytes, int radix){
            return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
        }

        /**Cipher.getInstance
         * AES加密
         * @param content 待加密的内容
         * @param encryptKey 加密密钥
         * @return 加密后的byte[]
         * @throws Exception
         */
        private static byte[] encryptToBytes(String content, String encryptKey) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128);
//        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
//        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));


            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            // 注意这句是关键，防止linux下 随机生成key。用其他方式在Windows上正常，但Linux上会有问题
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encryptKey.getBytes());
            kgen.init(128, secureRandom);
//        kgen.init(128);


//        SecretKey secretKey = kgen.generateKey();
//        byte[] enCodeFormat = secretKey.getEncoded();
//        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化

            return cipher.doFinal(content.getBytes("utf-8"));
        }
        /**
         * AES解密
         * @param encryptBytes 待解密的byte[]
         * @param decryptKey 解密密钥
         * @return 解密后的String
         * @throws Exception
         */
        private static String decryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            // 注意这句是关键，防止linux下 随机生成key。用其他方式在Windows上正常，但Linux上会有问题
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(decryptKey.getBytes());
            kgen.init(128, secureRandom);
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化

            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        }


        /**
         * 将base 64 code AES解密
         * @param encryptStr 待解密的base 64 code
         * @param decryptKey 解密密钥
         * @return 解密后的string
         * @throws Exception
         */
        public static String decrypt(String encryptStr, String decryptKey) {
            try {
                return StringUtils.isEmpty(encryptStr) ? "" : decryptByBytes(base64.decode(encryptStr), decryptKey);
            } catch (Exception ex) {
                return "";
            }
        }
        /**
         * AES加密为base 64 code
         * @param content 待加密的内容
         * @param encryptKey 加密密钥
         * @return 加密后的base 64 code
         * @throws Exception
         */
        public static String encrypt(String content, String encryptKey) {
            try {
                return base64.encode(encryptToBytes(content, encryptKey));
            } catch (Exception ex) {
                return "";
            }
        }


    }

    public static class base64 {

        private static String encode(byte[] bytes){
            return Base64.getEncoder().encodeToString(bytes);
        }

        private static byte[] decode(String base64Code) throws Exception{
            return StringUtils.isEmpty(base64Code) ? null : Base64.getDecoder().decode(base64Code);
        }
    }

    public class md5 {

        public static String md5(String text) throws Exception {
            byte[] bytes = text.getBytes();

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytes);
            bytes = messageDigest.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                if ((bytes[i] & 0xff) < 0x10) {
                    sb.append("0");
                }
                sb.append(Long.toString(bytes[i] & 0xff, 16));
            }
            return sb.toString().toLowerCase();
        }
    }
}