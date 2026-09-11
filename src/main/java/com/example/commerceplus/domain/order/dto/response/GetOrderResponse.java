package com.example.commerceplus.domain.order.dto.response;

import com.example.commerceplus.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public record GetOrderResponse(
        Long orderId,
        String orderNumber,
        Long paymentId,
        int totalPrice,
        String status,
        String orderName,
        LocalDateTime createdAt,
        List<GetOrderItemResponse> orderItems
) {
    public static GetOrderResponse from(Order order, Long paymentId) {
        List<GetOrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(GetOrderItemResponse::from)
                .toList();
        return new GetOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                paymentId,
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getCreatedAt(),
                orderItems
        );
    }
}
