package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

@Service
@Transactional
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    public User register(RegisterRequest request) {
        try {
            log.info("=== Starting registration process ===");
            log.info("Request details: username={}, email={}, isAdmin={}", 
                request.getUsername(), request.getEmail(), request.getAdminKey() != null);

            // 检查用户名是否存在
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessException("用户名已存在");
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            // 设置角色
            String adminKey = request.getAdminKey();
            if (adminKey != null && !adminKey.trim().isEmpty()) {
                log.info("Processing admin registration attempt");
                log.info("Admin key provided: [{}]", adminKey);
                log.info("Expected admin key: [{}]", adminSecretKey);
                
                // 比较密钥前先去除空白字符
                String providedKey = adminKey.trim();
                String configuredKey = adminSecretKey.trim();
                
                boolean keysMatch = providedKey.equals(configuredKey);
                log.info("Keys match: {}", keysMatch);

                if (!keysMatch) {
                    log.warn("Admin key mismatch");
                    throw new BusinessException("管理员密钥不正确");
                }

                log.info("Admin key verified, setting ADMIN role");
                user.setRole(UserRole.ADMIN);
            } else {
                log.info("No admin key provided, setting USER role");
                user.setRole(UserRole.USER);
            }

            // 保存前检查
            log.info("Pre-save state - Username: {}, Role: {}", user.getUsername(), user.getRole());

            // 保存用户
            User savedUser = userRepository.save(user);

            // 验证保存结果
            log.info("Post-save state - ID: {}, Username: {}, Role: {}", 
                savedUser.getId(), savedUser.getUsername(), savedUser.getRole());

            // 从数据库重新查询以验证
            User verifiedUser = userRepository.findById(savedUser.getId()).orElse(null);
            if (verifiedUser != null) {
                log.info("Verified from database - ID: {}, Username: {}, Role: {}", 
                    verifiedUser.getId(), verifiedUser.getUsername(), verifiedUser.getRole());
            }

            log.info("=== Registration completed successfully ===");
            return savedUser;

        } catch (Exception e) {
            log.error("Registration failed", e);
            throw e;
        }
    }

    public String login(LoginRequest request) {
        try {
            log.info("Attempting login for user: {}", request.getUsername());
            
            // 尝试认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
            
            // 生成token
            String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
            
            log.info("Login successful for user: {}, role: {}", user.getUsername(), user.getRole().name());
            return token;
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new BusinessException("用户名或密码错误");
        } catch (Exception e) {
            log.error("Login failed with unexpected error", e);
            throw new BusinessException("登录失败，请稍后重试");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        // 打印日志以便调试
        log.debug("Current user role: {}", user.getRole());
        return user;
    }
} 