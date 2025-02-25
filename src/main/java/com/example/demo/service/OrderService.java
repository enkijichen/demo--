package com.example.demo.service;

import com.example.demo.dto.OrderRequest;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Textbook;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.TextbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private TextbookRepository textbookRepository;
    
    @Autowired
    private UserService userService;

    public Order createOrder(User user, OrderRequest request) {
        try {
            log.info("Creating order for user: {}", user.getUsername());
            
            Textbook textbook = textbookRepository.findById(request.getTextbookId())
                .orElseThrow(() -> new BusinessException("教材不存在"));
                
            if (textbook.getStock() < request.getQuantity()) {
                throw new BusinessException("库存不足");
            }
            
            Order order = new Order();
            order.setUser(user);
            order.setTextbook(textbook);
            order.setQuantity(request.getQuantity());
            order.setReceiver(request.getReceiver());
            order.setPhone(request.getPhone());
            order.setAddress(request.getAddress());
            order.setStatus(OrderStatus.PENDING);
            
            // 减少库存
            textbook.setStock(textbook.getStock() - request.getQuantity());
            textbookRepository.save(textbook);
            
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully: {}", savedOrder.getId());
            return savedOrder;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create order", e);
            throw new BusinessException("创建订单失败");
        }
    }

    public List<Order> getMyOrders(String username) {
        try {
            log.info("Getting orders for user: {}", username);
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
            return orderRepository.findByUserOrderByCreateTimeDesc(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get orders for user: {}", username, e);
            throw new BusinessException("获取订单列表失败");
        }
    }

    public List<Order> findPendingOrders() {
        return orderRepository.findByStatusOrderByCreateTimeDesc(OrderStatus.PENDING);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        try {
            log.info("Updating order status: orderId={}, status={}", orderId, status);
            
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
                
            OrderStatus newStatus = OrderStatus.fromString(status);
            OrderStatus currentStatus = order.getStatus();
            
            log.info("Current status: {}, New status: {}", currentStatus, newStatus);
            
            // 检查状态转换是否合法
            if (currentStatus != OrderStatus.PENDING && 
                !(currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.RECEIVED)) {
                throw new BusinessException("当前订单状态不可修改");
            }
            
            // 更新订单状态
            order.setStatus(newStatus);
            
            // 处理库存
            Textbook textbook = order.getTextbook();
            
            // 如果是取消订单，恢复库存
            if (newStatus == OrderStatus.CANCELLED && currentStatus == OrderStatus.PENDING) {
                log.info("Restoring stock for cancelled order: quantity={}", order.getQuantity());
                textbook.setStock(textbook.getStock() + order.getQuantity());
                textbookRepository.save(textbook);
            }
            
            Order updatedOrder = orderRepository.save(order);
            log.info("Order status updated successfully: {}", updatedOrder.getId());
            return updatedOrder;
            
        } catch (BusinessException e) {
            log.warn("Failed to update order status: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating order status", e);
            throw new BusinessException("更新订单状态失败");
        }
    }
} 