package com.accounting.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 * 测试 JWT token 生成、解析、验证功能
 */
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private static final String SECRET_KEY = "mySecretKey123456789012345678901234567890";
    private static final Long EXPIRATION = 86400000L; // 24 hours
    private static final Long USER_ID = 123L;
    private static final String USERNAME = "testuser";
    private static final String ROLE = "USER";

    @BeforeEach
    void setUp() {
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    void testGenerateToken_Success() {
        // 执行测试
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 验证
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT token 通常以 eyJ 开头（Base64 编码的头部）
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void testGetClaimsFromToken_Success() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        Claims claims = jwtUtil.getClaimsFromToken(token);

        // 验证
        assertNotNull(claims);
        assertEquals(USER_ID.toString(), claims.getSubject());
        assertEquals(USERNAME, claims.get("username"));
        assertEquals(ROLE, claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void testGetUserIdFromToken_Success() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 验证
        assertEquals(USER_ID, userId);
    }

    @Test
    void testGetUsernameFromToken_Success() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        String username = jwtUtil.getUsernameFromToken(token);

        // 验证
        assertEquals(USERNAME, username);
    }

    @Test
    void testGetRoleFromToken_Success() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        String role = jwtUtil.getRoleFromToken(token);

        // 验证
        assertEquals(ROLE, role);
    }

    @Test
    void testIsTokenExpired_False() {
        // 准备测试数据：新生成的 token
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // 验证：刚生成的 token 不应该过期
        assertFalse(isExpired);
    }

    @Test
    void testValidateToken_ValidToken() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        // 执行测试
        boolean isValid = jwtUtil.validateToken(token);

        // 验证
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // 准备测试数据：无效的 token
        String invalidToken = "invalid.token.string";

        // 执行测试
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // 验证
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken() {
        // 执行测试
        boolean isValid = jwtUtil.validateToken(null);

        // 验证
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken() {
        // 执行测试
        boolean isValid = jwtUtil.validateToken("");

        // 验证
        assertFalse(isValid);
    }

    @Test
    void testGenerateToken_WithDifferentUsers() {
        // 准备测试数据
        String token1 = jwtUtil.generateToken(USER_ID, "user1", "USER");
        String token2 = jwtUtil.generateToken(456L, "user2", "LEADER");

        // 执行测试
        Long userId1 = jwtUtil.getUserIdFromToken(token1);
        Long userId2 = jwtUtil.getUserIdFromToken(token2);
        String username1 = jwtUtil.getUsernameFromToken(token1);
        String username2 = jwtUtil.getUsernameFromToken(token2);
        String role1 = jwtUtil.getRoleFromToken(token1);
        String role2 = jwtUtil.getRoleFromToken(token2);

        // 验证：两个 token 应该包含不同的信息
        assertEquals(USER_ID, userId1);
        assertEquals(456L, userId2);
        assertEquals("user1", username1);
        assertEquals("user2", username2);
        assertEquals("USER", role1);
        assertEquals("LEADER", role2);
        // 两个 token 应该是不同的
        assertNotEquals(token1, token2);
    }

    @Test
    void testGetClaimsFromToken_InvalidToken() {
        // 准备测试数据：无效的 token
        String invalidToken = "not.a.valid.jwt";

        // 执行测试并验证异常
        assertThrows(Exception.class, () -> {
            jwtUtil.getClaimsFromToken(invalidToken);
        });
    }

    @Test
    void testValidateToken_TamperedToken() {
        // 准备测试数据：生成 token 后修改它
        String originalToken = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);
        String tamperedToken = originalToken.substring(0, originalToken.length() - 5) + "12345";

        // 执行测试
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // 验证：被篡改的 token 应该无效
        assertFalse(isValid);
    }

    @Test
    void testTokenExpirationTime() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);
        Claims claims = jwtUtil.getClaimsFromToken(token);

        // 验证：过期时间应该是当前时间 + expiration
        long expectedExpiration = System.currentTimeMillis() + EXPIRATION;
        long actualExpiration = claims.getExpiration().getTime();

        // 允许 5 秒的误差（测试执行时间）
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 5000);
    }

    @Test
    void testTokenIssuedTime() {
        // 准备测试数据
        String token = jwtUtil.generateToken(USER_ID, USERNAME, ROLE);

        Claims claims = jwtUtil.getClaimsFromToken(token);
        long issuedTime = claims.getIssuedAt().getTime();

        // 验证：签发时间应该与当前时间相近（允许 5 秒误差）
        long currentTime = System.currentTimeMillis();
        assertTrue(Math.abs(currentTime - issuedTime) < 5000,
                "Token issued time should be close to current time");
    }

    @Test
    void testGenerateToken_WithChineseUsername() {
        // 准备测试数据：中文名用户名
        String chineseUsername = "张三";
        String token = jwtUtil.generateToken(USER_ID, chineseUsername, ROLE);

        // 执行测试
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // 验证：中文名应该能正确编码和解码
        assertEquals(chineseUsername, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_WithMalformedToken() {
        // 测试各种格式的错误 token
        String[] malformedTokens = {
                "invalid",
                "Bearer token",
                "three.parts.but.invalid",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0"
        };

        for (String token : malformedTokens) {
            assertFalse(jwtUtil.validateToken(token),
                    "Token should be invalid: " + token);
        }
    }
}
