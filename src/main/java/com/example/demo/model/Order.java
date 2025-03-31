package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")  // 不要用 order，因为是 SQL 关键字
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "orders", "hibernateLazyInitializer", "handler"})
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "textbook_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Textbook textbook;
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "收货人不能为空")
    @Column(nullable = false)
    private String receiver;
    
    @NotNull(message = "联系电话不能为空")
    @Column(nullable = false)
    private String phone;
    
    @NotNull(message = "地址不能为空")
    @Column(nullable = false)
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 