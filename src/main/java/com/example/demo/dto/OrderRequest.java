package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "教材ID不能为空")
    private Long textbookId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    @NotBlank(message = "收货人不能为空")
    private String receiver;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String phone;

    @NotBlank(message = "地址不能为空")
    private String address;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
} 