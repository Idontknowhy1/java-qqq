package com.jike.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtTokenUtil {
    // 假设的配置类，你需要根据实际情况获取密钥
    // private static final String JWT_KEY = global.Config.AppInfo.Secure.JwtKey;

    // 实际使用中，密钥应从安全配置中获取，这里使用示例密钥
    private static final String JWT_KEY = "=uZrS@Fgr!T7b@DpyTVzrWN7Rv6Ty9=W";
    private static final SecretKey MY_SIGNING_KEY = Keys.hmacShaKeyFor(JWT_KEY.getBytes());

    public static String generateToken(String value) {
        try {
            // 计算过期时间 (60天，与Go代码中的 time.Hour * 24 * 60 对应)
//            long expirationTimeMillis = System.currentTimeMillis() + (60 * 24 * 60 * 60 * 1000L);
//            Date expireAt = new Date(expirationTimeMillis);

            // 创建JWT声明
            Claims claims = Jwts.claims();
            claims.setIssuer("jike-app-server");
            claims.setId(UUID.randomUUID().toString());
            claims.setSubject(value);
            claims.setAudience("jike-app");
            claims.setIssuedAt(new Date());
//            claims.setExpiration(expireAt);

            // 创建并签名Token
            String token = Jwts.builder()
                    .setClaims(claims)
                    .signWith(MY_SIGNING_KEY, SignatureAlgorithm.HS256)
                    .compact();

            return token;
        } catch (Exception e) {
            return null;
        }
    }

    public static String parseToken(String token) throws Exception {
        try {
            // 解析Token并验证签名
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(MY_SIGNING_KEY)
                    .build();

            // 解析Token，获取Claims
            Jws<Claims> jws = parser.parseClaimsJws(token);
            Claims claims = jws.getBody();

            // 将标准Claims转换为CustomClaims
//            JwtTokenParser.CustomClaims customClaims = new JwtTokenParser.CustomClaims();

            // 设置标准声明字段
//            customClaims.setIssuer(claims.getIssuer());
//            customClaims.setId(claims.getId());
//            customClaims.setSubject(claims.getSubject());
//            customClaims.setAudience(claims.getAudience());
//            customClaims.setIssuedAt(claims.getIssuedAt());
//            customClaims.setExpiration(claims.getExpiration());
//            customClaims.setNotBefore(claims.getNotBefore());
//
//            // 设置自定义字段
//            customClaims.setDeviceId(claims.get("deviceId", String.class));

            return claims.getSubject();

        } catch (Exception e) {
//            throw new Exception("Token解析失败", e);
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        // 生成Token
////        String token = JwtTokenUtil.generateToken("123");
//
//        String token = parseToken("eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3R1cy1hcHAtc2VydmVyIiwianRpIjoiNzVlNmY5N2YtYTc5NC00ODRkLTg1NmYtYjNhYzJmYmQ1MzQ1Iiwic3ViIjoiMTIzIiwiYXVkIjoibG90dXMtYXBwIiwiaWF0IjoxNzU3NTc5OTQ2fQ.FAgb14kPNyifBEwb78UX4hwtiIKNVZn3qO_QpBujnmI");
//
//        System.out.println("生成的Token: " + token);
//    }
}
