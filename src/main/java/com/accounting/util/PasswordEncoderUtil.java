package com.accounting.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码工具类
 * 用于生成 BCrypt 加密的密码
 */
public class PasswordEncoderUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        System.out.println("admin123: " + encode("admin123"));
        System.out.println("leader123: " + encode("leader123"));
        System.out.println("employee123: " + encode("employee123"));
    }
}
