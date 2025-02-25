package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Table(name = "user")
public class User implements UserDetails {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Column(nullable = false)
    private String password;
    
    @Email(message = "邮箱格式不正确")
    @Column(nullable = false)
    private String email;
    
    @Pattern(regexp = "^[0-9]{11}$", message = "手机号必须为11位数字")
    @Column(nullable = false)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setRole(UserRole role) {
        log.info("Setting role: {} (value: {})", role.name(), role.getValue());
        this.role = role;
    }

    public UserRole getRole() {
        String roleName = role != null ? role.name() : "null";
        String roleValue = role != null ? role.getValue() : "null";
        log.info("Getting role: {} (value: {})", roleName, roleValue);
        return this.role;
    }
} 