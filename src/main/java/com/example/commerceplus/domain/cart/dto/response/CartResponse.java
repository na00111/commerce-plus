package com.example.commerceplus.domain.cart.dto.response;

import com.example.commerceplus.domain.cart.entity.CartItem;

import java.util.List;

public record CartResponse ( // 장바구니에 담긴 상품 리스트
                             List<CartItemResponse> cartItems,
                             // 장바구니에 담긴 상품 가격*수량의 총합
                             int totalPrice) {
    public static CartResponse from(List<CartItem> cartItems) {
        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(CartItemResponse::from)
                .toList();
        int calculatedTotalPrice = itemResponses.stream()
                .mapToInt(CartItemResponse::totalPrice)
                .sum();

        // 3. CartResponse 생성 후 반환
        return new CartResponse(itemResponses, calculatedTotalPrice);
    }
}
