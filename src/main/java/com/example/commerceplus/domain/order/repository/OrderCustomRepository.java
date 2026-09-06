package com.example.commerceplus.domain.order.repository;

import com.example.commerceplus.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomRepository {
    Page<Order> findMyOrders(Long memberId, Pageable pageable);
}
