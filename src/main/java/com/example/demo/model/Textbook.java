package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "textbook")
public class Textbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "教材名称不能为空")
    private String name;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @NotBlank(message = "科目不能为空")
    private String subject;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格必须大于等于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存必须大于等于0")
    private Integer stock;

    private String imageUrl;
} 