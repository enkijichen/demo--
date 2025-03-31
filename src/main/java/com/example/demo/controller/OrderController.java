package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.UpdateOrderStatusRequest;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody @Valid OrderRequest request) {
        try {
            log.info("Creating order for user: {}", request.getUsername());
            User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));
            
            Order order = orderService.createOrder(user, request);
            return ResponseEntity.ok(ApiResponse.success(order, "订单创建成功"));
        } catch (BusinessException e) {
            log.warn("Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Order creation failed with unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("订单创建失败，请稍后重试"));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(@RequestParam String username) {
        try {
            log.info("Getting orders for user: {}", username);
            List<Order> orders = orderService.getMyOrders(username);
            
            // 手动触发加载关联实体
            orders.forEach(order -> {
                order.getTextbook().getName(); // 触发 textbook 加载
                order.getUser().getUsername(); // 触发 user 加载
            });
            
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (BusinessException e) {
            log.warn("Failed to get orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取订单列表失败"));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Order>>> getPendingOrders(@RequestParam String username) {
        try {
            log.info("Getting pending orders for admin: {}", username);
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
            
            if (user.getRole() != UserRole.ADMIN) {
                throw new BusinessException("无权限访问");
            }
            
            List<Order> orders = orderService.findPendingOrders();
            
            // 手动触发加载关联实体
            orders.forEach(order -> {
                order.getTextbook().getName(); // 触发 textbook 加载
                order.getUser().getUsername(); // 触发 user 加载
            });
            
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (BusinessException e) {
            log.warn("Failed to get pending orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get pending orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取待处理订单失败"));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        try {
            Order order = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (BusinessException e) {
            log.warn("Failed to update order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update order status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("更新订单状态失败"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(@RequestParam String username) {
        try {
            log.info("Getting all orders for admin: {}", username);
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
            
            if (user.getRole() != UserRole.ADMIN) {
                throw new BusinessException("无权限访问");
            }
            
            List<Order> orders = orderService.findAllOrders();
            
            // 手动触发加载关联实体
            orders.forEach(order -> {
                order.getTextbook().getName(); // 触发 textbook 加载
                order.getUser().getUsername(); // 触发 user 加载
            });
            
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (BusinessException e) {
            log.warn("Failed to get orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to get orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取订单列表失败"));
        }
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<Order>> payOrder(@PathVariable Long orderId) {
        try {
            log.info("Processing payment for order: {}", orderId);
            Order order = orderService.payOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(order, "支付成功"));
        } catch (BusinessException e) {
            log.warn("Payment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Payment failed with unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("支付失败，请稍后重试"));
        }
    }
} 