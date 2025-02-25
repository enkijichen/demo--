package com.example.demo.service;

import com.example.demo.dto.OrderRequest;
import com.example.demo.model.Order;
import com.example.demo.model.Textbook;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.TextbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import com.example.demo.model.OrderStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import com.example.demo.service.TextbookService;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private TextbookRepository textbookRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private TextbookService textbookService;
    
    @InjectMocks
    private OrderService orderService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void createOrder_Success() {
        // 准备测试数据
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Textbook textbook = new Textbook();
        textbook.setId(1L);
        textbook.setStock(10);

        OrderRequest request = new OrderRequest();
        request.setTextbookId(1L);
        request.setQuantity(2);
        request.setReceiver("Test Receiver");
        request.setPhone("13800138000");
        request.setAddress("Test Address");
        request.setUsername("testUser");

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUser(user);
        savedOrder.setTextbook(textbook);

        // 设置模拟行为
        when(textbookRepository.findById(1L)).thenReturn(Optional.of(textbook));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // 执行测试
        Order result = orderService.createOrder(user, request);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(textbookRepository).save(any(Textbook.class));
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    void createOrder_InsufficientStock() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setTextbookId(1L);
        request.setQuantity(2);
        request.setAddress("测试地址");

        User user = new User();
        user.setId(1L);
        
        Textbook textbook = new Textbook();
        textbook.setId(1L);
        textbook.setStock(1);
        
        when(userService.getCurrentUser()).thenReturn(user);
        when(textbookService.findById(1L)).thenReturn(Optional.of(textbook));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> 
            orderService.createOrder(user, request)
        );
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        Textbook textbook = new Textbook();
        textbook.setStock(10);
        order.setTextbook(textbook);
        order.setQuantity(2);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Act
        Order result = orderService.updateOrderStatus(1L, "SHIPPED");
        
        // Assert
        assertEquals(OrderStatus.SHIPPED, result.getStatus());
        verify(textbookService).save(textbook);
    }

    @Test
    void updateOrderStatus_Reject() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        Textbook textbook = new Textbook();
        textbook.setStock(10);
        order.setTextbook(textbook);
        order.setQuantity(2);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Act
        Order result = orderService.updateOrderStatus(1L, "REJECTED");
        
        // Assert
        assertEquals(OrderStatus.REJECTED, result.getStatus());
        verify(textbookService).save(textbook);
    }
} 