package com.example.commerceplus.domain.cart.service;

import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.entity.CartItem;

import com.example.commerceplus.domain.cart.repository.CartItemRepository;
import com.example.commerceplus.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@RequiredArgsConstructor
@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void addItem(Cart cart,
                        Product product,
                        int quantity) {
        // Cart와 Product조합의 CartItem 이 있는지 확인
        Optional<CartItem> foundCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        // Cart와 Product조합이 없을 경우 생성
        if (foundCartItem.isEmpty()) {
            CartItem cartItem = foundCartItem.get();
            cartItemRepository.save(cartItem);
            return;
        }
        // Cart와 Product조합이 있을 경우 수량을 더한다.
        foundCartItem.get().addQuantity(quantity);
        cartItemRepository.save(foundCartItem.get());
    }

    @Transactional
    public int getExistingQuantity(Cart cart, Product product) {
        Integer countedQuantity = cartItemRepository.sumQuantityByCartAndProduct(cart, product);
        if (countedQuantity == null) {
            return 0;
        }
        return countedQuantity;
    }
}