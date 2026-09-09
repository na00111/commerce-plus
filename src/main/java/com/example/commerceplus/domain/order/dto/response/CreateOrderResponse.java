package com.example.commerceplus.domain.order.dto.response;

import com.example.commerceplus.domain.order.entity.Order;

import java.time.LocalDateTime;

public record CreateOrderResponse(
        Long id,
        String orderNumber,
        Integer totalPrice,
        String status,
        LocalDateTime createdAt
) {
    // 정적 팩토리 메서드는 Record 내부에 위치해야 함
    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getStatus().toString(),
                order.getCreatedAt()
        );
    }
}