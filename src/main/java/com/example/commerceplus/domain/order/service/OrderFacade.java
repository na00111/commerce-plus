package com.example.commerceplus.domain.order.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.cart.entity.Cart;
import com.example.commerceplus.domain.cart.entity.CartItem;
import com.example.commerceplus.domain.cart.service.CartItemService;
import com.example.commerceplus.domain.cart.service.CartService;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.member.sevice.MemberService;
import com.example.commerceplus.domain.order.dto.request.CreateOrderRequest;
import com.example.commerceplus.domain.order.dto.response.CreateOrderResponse;
import com.example.commerceplus.domain.order.dto.response.GetCheckoutResponse;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderItem;
import com.example.commerceplus.domain.payment.entity.Payment;
import com.example.commerceplus.domain.payment.service.PaymentService;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final CartService cartService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final CartItemService cartItemService;

    public GetCheckoutResponse getCheckoutOne(Long memberId, List<Long> cartItemIds) {
        Member member = memberService.findMemberById(memberId);
        Cart cart = cartService.findCart(member.getId()).orElseThrow( () -> new BusinessException(ErrorCode.CART_NOT_FOUND));
        List<CartItem> cartItems = cartItemService.findAndValidateCartItems(cart, cartItemIds);
        List<GetCheckoutResponse.CheckoutItem> items = cartItems.stream()
                .map(GetCheckoutResponse.CheckoutItem::from)
                .toList();

        int totalPrice = items.stream()
                .mapToInt(GetCheckoutResponse.CheckoutItem::subtotal)
                .sum();

        return GetCheckoutResponse.of(items, totalPrice);
    }

    @Transactional
    public CreateOrderResponse createOrder(Long memberId, CreateOrderRequest request) {
        Member member = memberService.findMemberById(memberId);
        Cart cart = cartService.findCart(member.getId()).orElseThrow( () -> new BusinessException(ErrorCode.CART_NOT_FOUND));
        List<CartItem> cartItems = cartItemService.findAndValidateCartItems(cart, request.cartItemIds());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productService.findProductById(cartItem.getProductId());
            product.decreaseStock(cartItem.getQuantity());
            orderItems.add(new OrderItem(product, product.getPrice(), cartItem.getQuantity()));
        }

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getSubtotal)
                .sum();
        Order order = orderService.createOrder(member, orderItems, totalPrice);
        Payment payment = PaymentService.createPayment(order, totalPrice);

        // 결제 성공 시점까지 장바구니는 유지한다.
        return CreateOrderResponse.from(order, payment);
    }
}
