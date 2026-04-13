package com.jike.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtil {
    /**
     * 过期时间7天
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    /**
     * token私钥
     */
    private static final String TOKEN_SECRET = "176d159ed780b7760784c8cbf4dc8c8d";

    public static class mini {
        public  static String sign(String unionId, Integer userId){
            try{
                //过期时间
                Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
                //私钥及加密
                Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
                //设置头部信息
                Map<String,Object> header = new HashMap<>();
                header.put("typ","JWT");
                header.put("alg","H5256");
                //附带username、password
                String token = JWT.create()
                        .withHeader(header)
                        .withClaim("unionId",unionId)
                        .withClaim("userId",userId)
                        .withExpiresAt(date)
                        .sign(algorithm);
                return token;

            }catch (Exception ex){
                log.error("JWT签名失败");
                return null;
            }
        }
    }
}
