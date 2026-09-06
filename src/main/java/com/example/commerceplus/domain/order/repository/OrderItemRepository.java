package com.example.commerceplus.domain.order.repository;

import com.example.commerceplus.domain.order.entity.OrderItem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
