package com.example.commerceplus.domain.cart.facade;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.cart.dto.response.CartResponse;
import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.entity.CartItem;
import com.example.commerceplus.domain.cart.service.CartItemService;
import com.example.commerceplus.domain.cart.service.CartService;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.member.sevice.MemberService;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor

public class CartFacade {

    private final ProductService productService;
    private final MemberService memberService;
    private final CartService cartService;
    private final CartItemService cartItemService;


    public int addItem(Long memberId, Long productId, int quantity) {
        //수량 확인
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        // 로그인한 회원 조회
        Member member = memberService.findMemberById(memberId);
        //상품 있는지
        Product product = productService.findProductById(productId);
        // 회원의 장바구니 조회
        // 장바구니가 없다면 새로 생성
        Cart cart = cartService.findOrCreateCart(member);

        // 장바구니에 담기 상품의 재고보다 장바구니에 담은 수량이 더 많은지 검증하는 로직
        int currentCartItemQuantity = cartItemService.getExistingQuantity(cart, product);
        int totalQuantity = currentCartItemQuantity + quantity;

       return cartItemService.addItem(cart, product, quantity);
    }

    public CartResponse getCart(Long memberId) {

        Optional<Cart> cart = cartService.findCart(memberId);

        if (cart.isEmpty()) {
            return new CartResponse(null, List.of());
        }
        return cartItemService.getCartItems(cart.get());
    }

    public  int UpdateCartItemQuantity(Long memberId, Long productId, int quantity) {
        //회원 조회
        Member member = memberService.findMemberById(memberId);
        //회원의 장바구니 조회
        Cart cart = cartService.findCart(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
        return cartItemService.UpdateQuantity(cart, productId, quantity);
    }

    public void deleteCartItem(Long memberId, Long cartItemId) {
        //회원 존재 확인
        memberService.findMemberById(memberId);
        //장바구 상품 삭제
        cartItemService.deleteCatItem(memberId, cartItemId);

    }
}
