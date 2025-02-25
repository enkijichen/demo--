package com.example.demo.model;

public enum OrderStatus {
    PENDING("待审核"),
    SHIPPED("已发货"),
    RECEIVED("已收货"),
    REJECTED("已拒绝"),
    CANCELLED("已撤回");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
} 