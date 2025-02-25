package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody UpdatePasswordRequest request) {
        try {
            log.info("Starting password update process for request");
            
            // 从请求中获取用户名
            String username = request.getUsername();
            log.info("Username from request: {}", username);
            
            // 从数据库获取用户
            User user = userService.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new BusinessException("用户不存在");
                });
            
            log.info("User found in database: {}", user.getUsername());
            
            // 更新密码
            userService.updatePassword(user, request.getOldPassword(), request.getNewPassword());
            log.info("Password updated successfully for user: {}", user.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success(null, "密码修改成功"));
        } catch (BusinessException e) {
            log.warn("Business error during password update: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("密码修改失败，请稍后重试"));
        }
    }
}

class UpdatePasswordRequest {
    private String username;
    private String oldPassword;
    private String newPassword;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
} 