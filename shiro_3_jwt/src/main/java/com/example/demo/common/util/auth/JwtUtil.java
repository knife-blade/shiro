package com.example.demo.common.util.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo.common.util.ApplicationContextHolder;
import com.example.demo.config.properties.JwtProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtUtil {
    private static final JwtProperties jwtProperties;
    private static final JWTVerifier jwtVerifier;

    static {
        jwtProperties = ApplicationContextHolder.getContext().getBean(JwtProperties.class);
        jwtVerifier = createVerifier();
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

    /**
     * 校验token
     * 若校验失败，会抛出异常：{@link JWTVerificationException}
     * 失败情况（按先后顺序）：
     * - 算法不匹配：{@link com.auth0.jwt.exceptions.AlgorithmMismatchException}
     * - 签名验证失败：{@link com.auth0.jwt.exceptions.SignatureVerificationException}
     * - Claim无效：{@link com.auth0.jwt.exceptions.InvalidClaimException}
     * - token超期：{@link com.auth0.jwt.exceptions.TokenExpiredException}
     */
    public static boolean verifyToken(String token) {
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            return false;
        }

        return true;
    }

    public static String getUserIdByToken(String token) {
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            return userId;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        try {
            jwtVerifier.verify(token);
        } catch (TokenExpiredException e) {
            return true;
        }
        return false;
    }

    private static JWTVerifier createVerifier() {
        Algorithm algorithm = Algorithm.HMAC512(jwtProperties.getSecret());

        return JWT.require(algorithm)
                // .withIssuer("auth0")
                // .withClaim("userName", userName)
                .build();
    }
}
