package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        try {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });
            
            log.debug("User found: {}, role: {}", user.getUsername(), user.getRole());
            
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (Exception e) {
            log.error("Error loading user: {}", username, e);
            throw e;
        }
    }

    @Transactional
    public User register(RegisterRequest request) {
        log.info("Starting registration process for user: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // 处理管理员注册
        if (request.getAdminKey() != null && !request.getAdminKey().trim().isEmpty()) {
            log.info("Admin registration attempt with key provided");
            String expectedKey = adminSecretKey; // 从配置中获取
            
            if (expectedKey.equals(request.getAdminKey().trim())) {
                log.info("Admin key verified, setting ADMIN role");
                user.setRole(UserRole.ADMIN);
            } else {
                log.warn("Invalid admin key provided");
                throw new BusinessException("Invalid admin key");
            }
        } else {
            log.info("Setting regular USER role");
            user.setRole(UserRole.USER);
        }

        log.info("Saving user with role: {}", user.getRole());
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void updatePassword(User user, String oldPassword, String newPassword) {
        log.info("Attempting to update password for user: {}", user.getUsername());
        
        // 验证用户是否存在
        User currentUser = userRepository.findByUsername(user.getUsername())
            .orElseThrow(() -> {
                log.warn("User not found during password update: {}", user.getUsername());
                return new BusinessException("用户不存在");
            });
        
        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            log.warn("Old password verification failed for user: {}", user.getUsername());
            throw new BusinessException("原密码不正确");
        }

        // 验证新密码不能与旧密码相同
        if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
            log.warn("New password is same as old password for user: {}", user.getUsername());
            throw new BusinessException("新密码不能与原密码相同");
        }

        // 更新密码
        log.info("Updating password for user: {}", user.getUsername());
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        log.info("Password updated successfully for user: {}", user.getUsername());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication found in SecurityContext");
            throw new BusinessException("用户未登录");
        }
        
        String username = authentication.getName();
        log.debug("Getting current user info for: {}", username);
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("User not found in database: {}", username);
                return new BusinessException("用户不存在");
            });
    }
} 