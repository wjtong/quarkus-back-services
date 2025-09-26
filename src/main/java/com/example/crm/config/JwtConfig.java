package com.example.crm.config;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * JWT 配置类
 * 负责 JWT Token 的生成和配置
 */
@ApplicationScoped
public class JwtConfig {

    @ConfigProperty(name = "jwt.issuer", defaultValue = "crm-service")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.time", defaultValue = "3600")
    Long expirationTime;

    /**
     * 生成 JWT Token
     */
    public String generateToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Duration.ofSeconds(expirationTime));

        return Jwt.issuer(issuer)
                .subject(username)
                .groups(roles)
                .issuedAt(now)
                .expiresAt(expiration)
                .sign();
    }

    /**
     * 生成刷新 Token
     */
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(Duration.ofDays(7)); // 刷新 token 有效期 7 天

        return Jwt.issuer(issuer)
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiresAt(expiration)
                .sign();
    }

    /**
     * 获取 Token 过期时间（秒）
     */
    public Long getExpirationTime() {
        return expirationTime;
    }

    /**
     * 获取发行者
     */
    public String getIssuer() {
        return issuer;
    }
}
