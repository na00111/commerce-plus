package com.example.commerceplus.domain.order.dto.response;

import com.example.commerceplus.domain.cart.entity.CartItem;

import java.util.List;

public class GetCheckoutResponse {

    public static GetCheckoutResponse of(List<CheckoutItem> items, int totalPrice) {
        return null;
    }

    public static class CheckoutItem {
       public static CheckoutItem from(CartItem cartItem) {
           return null;
       }

       public int subtotal() {
           return 0;
       }
   }
}
