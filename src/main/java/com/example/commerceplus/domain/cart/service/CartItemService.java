package com.example.commerceplus.domain.cart.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.cart.dto.response.CartItemResponse;
import com.example.commerceplus.domain.cart.dto.response.CartResponse;
import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.entity.CartItem;

import com.example.commerceplus.domain.cart.repository.CartItemRepository;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    @Transactional
    public int addItem(Cart cart,
                        Product product,
                        int quantity) {
        // Cart와 Product조합의 CartItem 이 있는지 확인
        Optional<CartItem> foundCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        // 신규 생성인 경우
        if (foundCartItem.isEmpty()) {
            CartItem newCartItem = CartItem.createCartItem(cart, product, quantity);
            cartItemRepository.save(newCartItem);
            return newCartItem.getQuantity();
        }

        // 이미 존재하는 경우
        CartItem existingCartItem = foundCartItem.get();
        existingCartItem.addQuantity(quantity);
        return existingCartItem.getQuantity();
    }

    //장바구에 담긴 상품 조회
   @Transactional(readOnly = true)
   public CartResponse getCartItems(Cart cart) {
         List<CartItem> cartItems = cartItemRepository.findByCart(cart);
         return CartResponse.from(cartItems);
}
    @Transactional(readOnly = true )//장바구니에 상품이 몇개 담겼는지
    public int getExistingQuantity(Cart cart, Product product) {
        Integer countedQuantity = cartItemRepository.sumQuantityByCartAndProduct(cart, product);
        return (countedQuantity != null)? countedQuantity : 0;
    }
    @Transactional
    public int UpdateQuantity(Cart cart, Long cartItemId, int quantity) {
        //타인의 아이템 수정 방지
        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId,cart)
                .orElseThrow(()->new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        //수량 변경
        cartItem.changeQuantity(quantity);
        return cartItem.getQuantity();
    }

    private List<CartItem> findAndValidateCartItems(Long memberId, List<Long> cartItemIds) {
        return null;
    }
}