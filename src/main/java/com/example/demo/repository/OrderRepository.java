package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreateTimeDesc(User user);
    List<Order> findByStatusOrderByCreateTimeDesc(OrderStatus status);
} 