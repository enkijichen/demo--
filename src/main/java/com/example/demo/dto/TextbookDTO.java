package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TextbookDTO {
    private String name;
    private String grade;
    private String subject;
    private BigDecimal price;
    private Integer stock;
} 