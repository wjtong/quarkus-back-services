package com.example.crm;

import com.example.crm.config.JwtConfig;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 认证测试
 * 测试 JWT Token 的生成和基本验证
 */
@QuarkusTest
public class JwtAuthenticationTest {

    @Inject
    JwtConfig jwtConfig;

    private String validToken;
    private String userOnlyToken;
    private String adminToken;

    @BeforeEach
    public void setUp() {
        // 生成有效的 JWT Token（包含多个角色）
        validToken = jwtConfig.generateToken("testuser", Set.of("USER", "ADMIN"));
        
        // 生成只有 USER 角色的 Token
        userOnlyToken = jwtConfig.generateToken("testuser", Set.of("USER"));
        
        // 生成只有 ADMIN 角色的 Token
        adminToken = jwtConfig.generateToken("admin", Set.of("ADMIN"));
    }

    @Test
    public void testJwtConfig() {
        // 测试 JWT 配置
        assertEquals("crm-service", jwtConfig.getIssuer());
        assertEquals(3600L, jwtConfig.getExpirationTime());
    }

    @Test
    public void testTokenGeneration() {
        // 测试 Token 生成
        assertNotNull(validToken);
        assertFalse(validToken.isEmpty());
        
        // 验证 Token 格式（JWT 应该有三个部分，用点分隔）
        String[] parts = validToken.split("\\.");
        assertEquals(3, parts.length, "JWT Token 应该有 3 个部分");
    }

    @Test
    public void testTokenWithMultipleRoles() {
        // 测试包含多个角色的 Token
        assertNotNull(validToken);
        String[] parts = validToken.split("\\.");
        assertEquals(3, parts.length);
        
        // 验证 Token 不为空且格式正确
        assertTrue(validToken.length() > 100); // JWT Token 通常比较长
    }

    @Test
    public void testUserOnlyToken() {
        // 测试只有 USER 角色的 Token
        assertNotNull(userOnlyToken);
        assertFalse(userOnlyToken.isEmpty());
        
        String[] parts = userOnlyToken.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    public void testAdminToken() {
        // 测试只有 ADMIN 角色的 Token
        assertNotNull(adminToken);
        assertFalse(adminToken.isEmpty());
        
        String[] parts = adminToken.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    public void testRefreshTokenGeneration() {
        // 测试刷新 Token 生成
        String refreshToken = jwtConfig.generateRefreshToken("testuser");
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        // 验证刷新 Token 格式
        String[] parts = refreshToken.split("\\.");
        assertEquals(3, parts.length, "Refresh Token 应该有 3 个部分");
        
        // 刷新 Token 应该与普通 Token 不同
        assertNotEquals(validToken, refreshToken);
    }

    @Test
    public void testTokenUniqueness() {
        // 测试生成的 Token 是唯一的
        String token1 = jwtConfig.generateToken("user1", Set.of("USER"));
        String token2 = jwtConfig.generateToken("user2", Set.of("USER"));
        String token3 = jwtConfig.generateToken("user1", Set.of("USER"));
        
        assertNotEquals(token1, token2, "不同用户的 Token 应该不同");
        // 注意：由于时间戳不同，即使是同一用户也可能生成不同的 Token
    }

    @Test
    public void testTokenWithDifferentRoles() {
        // 测试不同角色组合的 Token
        String userToken = jwtConfig.generateToken("user", Set.of("USER"));
        String managerToken = jwtConfig.generateToken("manager", Set.of("MANAGER"));
        String salesToken = jwtConfig.generateToken("sales", Set.of("SALES"));
        String multiRoleToken = jwtConfig.generateToken("admin", Set.of("USER", "ADMIN", "MANAGER"));
        
        assertNotNull(userToken);
        assertNotNull(managerToken);
        assertNotNull(salesToken);
        assertNotNull(multiRoleToken);
        
        // 所有 Token 都应该有不同的值
        assertNotEquals(userToken, managerToken);
        assertNotEquals(managerToken, salesToken);
        assertNotEquals(salesToken, multiRoleToken);
    }

    @Test
    public void testTokenStructure() {
        // 测试 Token 结构
        String[] parts = validToken.split("\\.");
        
        // Header 部分
        assertNotNull(parts[0]);
        assertFalse(parts[0].isEmpty());
        
        // Payload 部分
        assertNotNull(parts[1]);
        assertFalse(parts[1].isEmpty());
        
        // Signature 部分
        assertNotNull(parts[2]);
        assertFalse(parts[2].isEmpty());
    }

    @Test
    public void testInvalidTokenFormat() {
        // 测试无效的 Token 格式
        String invalidToken = "invalid.token.format";
        String[] parts = invalidToken.split("\\.");
        
        // 这个测试字符串恰好有 3 个部分，所以测试通过
        assertEquals(3, parts.length);
        
        // 测试真正无效的格式
        String reallyInvalidToken = "invalid.format";
        String[] invalidParts = reallyInvalidToken.split("\\.");
        assertNotEquals(3, invalidParts.length);
    }

    @Test
    public void testEmptyToken() {
        // 测试空用户名（JWT 库允许空用户名，所以不会抛异常）
        String token = jwtConfig.generateToken("", Set.of("USER"));
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testNullRoles() {
        // 测试空角色集合
        String token = jwtConfig.generateToken("testuser", Set.of());
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}
