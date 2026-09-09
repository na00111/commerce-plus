package com.example.commerceplus.domain.cart.facade;

import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.repository.CartRepository;
import com.example.commerceplus.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class CartFacade {

    private final CartRepository cartRepository;

    public Cart findOrCreateCart(Member member) {
        return cartRepository.findByMemberId(member.getId())
                .orElseGet(()-> cartRepository.save(Cart.create(member)));
    }
}
