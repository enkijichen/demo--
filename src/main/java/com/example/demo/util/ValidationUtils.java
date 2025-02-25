package com.example.demo.util;

import com.example.demo.constant.AppConstants;
import com.example.demo.exception.BadRequestException;

import java.util.Arrays;

public class ValidationUtils {
    
    public static void validateGrade(String grade) {
        if (!Arrays.asList(AppConstants.GRADES).contains(grade)) {
            throw new BadRequestException("无效的年级");
        }
    }
    
    public static void validateSubject(String subject) {
        if (!Arrays.asList(AppConstants.SUBJECTS).contains(subject)) {
            throw new BadRequestException("无效的科目");
        }
    }
    
    public static void validateQuantity(Integer quantity, Integer stock) {
        if (quantity > stock) {
            throw new BadRequestException("库存不足");
        }
    }
} 