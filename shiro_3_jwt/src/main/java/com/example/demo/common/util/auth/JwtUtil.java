package com.example.demo.common.util.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo.common.util.ApplicationContextHolder;
import com.example.demo.config.properties.JwtProperties;

import java.util.Date;

public class JwtUtil {
    private static final JwtProperties jwtProperties;

    static {
        jwtProperties = ApplicationContextHolder.getContext().getBean(JwtProperties.class);
    }

    // 创建jwt token
    public static String createToken(String userId) {
        try {
            Date date = new Date(System.currentTimeMillis() + jwtProperties.getExpire() * 1000);
            Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());
            return JWT.create()
                    // 自定义私有的payload的key-value。比如：.withClaim("userName", "Tony")
                    // .withClaim("key1", "value1")
                    .withAudience(userId)  // 将 user id 保存到 token 里面
                    .withExpiresAt(date)   // date之后，token过期
                    .sign(algorithm);      // token 的密钥
        } catch (Exception e) {
            return null;
        }
    }

    // 校验token
    public static boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());
            JWTVerifier verifier = JWT.require(algorithm)
                    // .withIssuer("auth0")
                    // .withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            // token错误、token超期等，都会走到这里
            return false;
        }
    }

    public static String getUserIdByToken(String token) {
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            return userId;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static boolean isTokenExpired() {

    }
}
