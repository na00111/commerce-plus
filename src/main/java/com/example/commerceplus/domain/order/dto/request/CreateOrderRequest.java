package com.example.commerceplus.domain.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "주문 상품은 최소 1개 이상이어야 합니다")
        @Valid
        List<OrderItemRequest> items
) {}
