package com.touchealth.platform.processengine.utils;

import com.touchealth.platform.processengine.constant.GlobalDefine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static String jwtSecret;

    @Value("${jwt.secret}")
    public void init(String jwtSecret) {
        JwtUtil.jwtSecret = jwtSecret;
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(jwtSecret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 创建jwt
     *
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     * @throws Exception
     */
    public static String createJWT(String id, String subject, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey key = generalKey();
        Map<String, Object> map = new HashMap<>();
        String refreshJWT = createRefreshJWT(id, subject, GlobalDefine.Jwt.JWT_REFRESH_TTL);
        map.put("rt", refreshJWT);
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(map)
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 创建刷新token
     *
     * @param id        tokenid
     * @param ttlMillis 有效时间
     * @return
     * @throws Exception
     */
    public static String createRefreshJWT(String id, String subject, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey key = generalKey();
        Map<String, Object> map = new HashMap<>();
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 解密jwt
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) {
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    /**
     * 获取用户ID
     *
     * @return
     * @throws Exception
     */
    public static Long getUserId(String token) throws Exception {
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        String userInfo = claims.getSubject();
        Map<String, String> usreMap = JsonUtil.getObjectFromJson(userInfo, Map.class);
        String userId = usreMap.get("userId");
        return Long.valueOf(userId);
    }

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map getUserInfo(String token) throws Exception {
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
        String userInfo = claims.getSubject();
        Map<String, String> userMap = JsonUtil.getObjectFromJson(userInfo, Map.class);
        return userMap;
    }
}
