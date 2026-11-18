package com.example.aihou.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aihou.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    // 构造器注入（原来用 static @Resource，static 字段 Spring 不会注入，会一直是 null）
    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // 1. 获取当前请求中的 Token（不依赖 jwtConfig，保持静态）
    public static String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return extractTokenFromRequest(attributes.getRequest());
        }
        return null;
    }

    // 2. 从请求头提取 Token（不依赖 jwtConfig，保持静态）
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String tokenHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        String simpleToken = request.getHeader("token");
        if (StringUtils.hasText(simpleToken)) {
            return simpleToken;
        }
        return null;
    }

    // 3. 解析并验证 Token，直接返回 DecodedJWT
    public DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("Token 不能为空");
        }
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtConfig.getIssuer()).build();
        return verifier.verify(token);
    }

    // 4. 生成 Token
    public String generateToken(Long userId, String username, Integer roleType) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("roleType", roleType)
                .withExpiresAt(expiration)
                .withIssuedAt(new Date())
                .withIssuer(jwtConfig.getIssuer())
                .sign(algorithm);
    }

    // 5. 验证 Token，返回校验结果（不抛异常）
    public TokenVerificationResult validateToken(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return new TokenVerificationResult(null, null, null, false);
            }
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtConfig.getIssuer()).build();
            DecodedJWT jwt = verifier.verify(token);

            Long userId = jwt.getClaim("userId").asLong();
            String username = jwt.getClaim("username").asString();
            Integer roleType = jwt.getClaim("roleType").asInt();

            return new TokenVerificationResult(userId, username, roleType, true);
        } catch (JWTVerificationException e) {
            return new TokenVerificationResult(null, null, null, false);
        }
    }

    @Getter
    public static class TokenVerificationResult {
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;

        public TokenVerificationResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
}
