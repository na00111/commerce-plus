package com.example.commerceplus.domain.cart.dto.response;


import com.example.commerceplus.domain.cart.entity.CartItem;

public record AddCartItemResponse(
        int cartItemQuantity
){
    public static AddCartItemResponse of(int cartItemQuantity) {
        return new AddCartItemResponse(cartItemQuantity);
    }
}
