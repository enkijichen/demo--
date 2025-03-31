package com.example.demo.model;

public enum OrderStatus {
    UNPAID("待支付"),
    PAID("已支付"),
    PENDING("待处理"),
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
            throw new IllegalArgumentException("无效的订单状态: " + status);
        }
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case UNPAID:
                return newStatus == PAID || newStatus == CANCELLED;
            case PAID:
                return newStatus == PENDING;
            case PENDING:
                return newStatus == SHIPPED || newStatus == REJECTED;
            case SHIPPED:
                return newStatus == RECEIVED;
            case RECEIVED:
            case REJECTED:
            case CANCELLED:
                return false; // 这些是终态
            default:
                return false;
        }
    }
} 