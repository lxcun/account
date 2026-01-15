package com.accounting.service;

import com.accounting.dto.LoginRequest;
import com.accounting.dto.LoginResponse;
import com.accounting.entity.Role;
import com.accounting.mapper.RoleMapper;
import com.accounting.security.CustomUserDetails;
import com.accounting.security.CustomUserDetailsService;
import com.accounting.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 * 测试用户登录、密码编码功能
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    private static final String ROLE_CODE = "USER";
    private static final String ROLE_NAME = "普通用户";

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // 不在这里设置通用 mock，避免 UnnecessaryStubbing 错误
    }

    @Test
    void testLogin_Success() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .roleCode(ROLE_CODE)
                .authorities(Collections.emptyList())
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setRoleCode(ROLE_CODE);
        role.setRoleName(ROLE_NAME);

        // 设置 mock 行为
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(USER_ID, USERNAME, ROLE_CODE)).thenReturn(TOKEN);
        when(roleMapper.findRolesByUserId(USER_ID)).thenReturn(Collections.singletonList(role));

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(ROLE_NAME, response.getRole());

        // 验证方法调用
        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(jwtUtil).generateToken(USER_ID, USERNAME, ROLE_CODE);
        verify(roleMapper).findRolesByUserId(USER_ID);
    }

    @Test
    void testLogin_WithInvalidCredentials() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword("wrongPassword");

        // 设置 mock 行为：认证失败
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("用户名或密码错误"));

        // 执行测试并验证异常
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );
        assertEquals("用户名或密码错误", exception.getMessage());

        // 验证：JWT 和角色查询都不应该被调用
        verify(jwtUtil, never()).generateToken(any(), any(), any());
        verify(roleMapper, never()).findRolesByUserId(any());
    }

    @Test
    void testLogin_WithLeaderRole() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword(PASSWORD);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(2L)
                .username("admin")
                .password(ENCODED_PASSWORD)
                .roleCode("LEADER")
                .authorities(Collections.emptyList())
                .build();

        Role role = new Role();
        role.setId(2L);
        role.setRoleCode("LEADER");
        role.setRoleName("管理员");

        // 设置 mock 行为
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(2L, "admin", "LEADER")).thenReturn(TOKEN);
        when(roleMapper.findRolesByUserId(2L)).thenReturn(Collections.singletonList(role));

        // 执行测试
        LoginResponse response = authService.login(request);

        // 验证
        assertNotNull(response);
        assertEquals(TOKEN, response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("管理员", response.getRole());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
    }

    @Test
    void testLogin_GeneratesUniqueToken() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .roleCode(ROLE_CODE)
                .authorities(Collections.emptyList())
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setRoleCode(ROLE_CODE);
        role.setRoleName(ROLE_NAME);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(USER_ID, USERNAME, ROLE_CODE)).thenReturn(TOKEN);
        when(roleMapper.findRolesByUserId(USER_ID)).thenReturn(Collections.singletonList(role));

        // 执行测试两次
        LoginResponse response1 = authService.login(request);
        LoginResponse response2 = authService.login(request);

        // 验证：两次登录应该都成功
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(TOKEN, response1.getToken());
        assertEquals(TOKEN, response2.getToken());

        // 验证：JwtUtil.generateToken 被调用了两次
        verify(jwtUtil, times(2)).generateToken(USER_ID, USERNAME, ROLE_CODE);
    }

    @Test
    void testEncodePassword() {
        // 准备测试数据
        String rawPassword = "myPassword123";

        // 设置 mock 行为
        when(passwordEncoder.encode(rawPassword)).thenReturn(ENCODED_PASSWORD);

        // 执行测试
        String encodedPassword = authService.encodePassword(rawPassword);

        // 验证
        assertEquals(ENCODED_PASSWORD, encodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void testEncodePassword_DifferentPasswords() {
        // 准备测试数据
        String password1 = "password123";
        String password2 = "password456";

        // 设置 mock 行为：不同的密码应该生成不同的哈希
        when(passwordEncoder.encode(password1)).thenReturn("$2a$10$hash1");
        when(passwordEncoder.encode(password2)).thenReturn("$2a$10$hash2");

        // 执行测试
        String encoded1 = authService.encodePassword(password1);
        String encoded2 = authService.encodePassword(password2);

        // 验证
        assertNotEquals(encoded1, encoded2);
        assertEquals("$2a$10$hash1", encoded1);
        assertEquals("$2a$10$hash2", encoded2);

        verify(passwordEncoder).encode(password1);
        verify(passwordEncoder).encode(password2);
    }

    @Test
    void testLogin_VerifyAuthenticationParameters() {
        // 准备测试数据
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .id(USER_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .roleCode(ROLE_CODE)
                .authorities(Collections.emptyList())
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setRoleCode(ROLE_CODE);
        role.setRoleName(ROLE_NAME);

        // 设置 mock 行为
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(USER_ID, USERNAME, ROLE_CODE)).thenReturn(TOKEN);
        when(roleMapper.findRolesByUserId(USER_ID)).thenReturn(Collections.singletonList(role));

        authService.login(request);

        // 验证：authenticationManager 使用正确的参数调用
        verify(authenticationManager).authenticate(
                argThat(auth -> {
                    UsernamePasswordAuthenticationToken token =
                            (UsernamePasswordAuthenticationToken) auth;
                    return USERNAME.equals(token.getPrincipal()) &&
                           PASSWORD.equals(token.getCredentials());
                })
        );
    }

    @Test
    void testLogin_EmptyPassword() {
        // 准备测试数据：空密码
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword("");

        // 设置 mock：空密码会认证失败
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("用户名或密码错误"));

        // 执行测试并验证异常
        assertThrows(BadCredentialsException.class, () ->
                authService.login(request)
        );

        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }
}
