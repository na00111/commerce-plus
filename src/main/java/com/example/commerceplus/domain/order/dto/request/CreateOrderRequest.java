package com.example.commerceplus.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(List<@NotNull Long> cartItemIds) {

        public CreateOrderRequest {
                cartItemIds = cartItemIds == null ? List.of() : List.copyOf(cartItemIds);
        }
}
