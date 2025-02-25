package com.example.demo.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {
    
    @Test
    public void generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成密码哈希
        String adminPass = "admin123";
        String userPass = "aka123";
        
        String adminHash = encoder.encode(adminPass);
        String userHash = encoder.encode(userPass);
        
        System.out.println("=== 新生成的密码哈希 ===");
        System.out.println("Admin (admin123) hash: " + adminHash);
        System.out.println("User (aka123) hash: " + userHash);
        
        // 验证密码
        assertTrue(encoder.matches(adminPass, adminHash), "Admin password verification failed");
        assertTrue(encoder.matches(userPass, userHash), "User password verification failed");
        
        // 验证当前使用的密码哈希
        String currentAdminHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt2s2YO";
        String currentUserHash = "$2a$10$k.Od3SU0.oD/vZmJZhKTYujM/U3j0LJgi7D4xGqKjbzHq.oXHdb1C";
        
        System.out.println("\n=== 验证当前密码哈希 ===");
        System.out.println("Admin password valid: " + encoder.matches(adminPass, currentAdminHash));
        System.out.println("User password valid: " + encoder.matches(userPass, currentUserHash));
    }
} 