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
import com.example.commerceplus.domain.order.dto.response.*;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderItem;
import com.example.commerceplus.domain.payment.entity.Payment;
import com.example.commerceplus.domain.payment.service.PaymentService;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class OrderFacade {

    private final CartService cartService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;
    private final CartItemService cartItemService;

    @Transactional(readOnly = true)
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

        return GetCheckoutResponse.from(items, totalPrice);
    }

    //생성할 주문 항목을 담는 빈 목록
    public CreateOrderResponse createOrder(Long memberId, CreateOrderRequest request) {

        Member member = memberService.findMemberById(memberId);
        Cart cart = cartService.findCart(member.getId()).orElseThrow( () -> new BusinessException(ErrorCode.CART_NOT_FOUND));
      
        List<CartItem> cartItems = cartItemService.findAndValidateCartItems(cart, request.cartItemIds());
        // 데드락 방지를 위해 ProductId로 정렬
        List<CartItem> sortedCartItems = cartItems.stream()
                .sorted(Comparator.comparing(cartItem -> cartItem.getProduct().getId()))
                .toList();
        List<OrderItem> orderItems = new ArrayList<>();

        //선택한 장바구니 항목을 주문 항목으로 변환
        for (CartItem cartItem : sortedCartItems) {
            // 이번 수정에서는 네가 사용하던 조회 메서드를 유지합니다.
            Product product = productService.findProductById(cartItem.getProductId());

            // 주문 수량만큼 재고를 선차감
            product.decreaseStock(cartItem.getQuantity());

            // 기존 3개 인자 생성자를 사용 상품, 주문 당시 가격, 주문 수량만 전달
            OrderItem orderItem = OrderItem.create(product, product.getPrice(), cartItem.getQuantity());

            // 생성한 주문 항목을 목록에 추가
            orderItems.add(orderItem);
        }

        //각 항목의 소계(가격 × 수량)를 합산
        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getSubtotal)
                .sum();

        //주문과 주문 항목을 저장
        Order order = orderService.createOrder(member, orderItems, totalPrice);

        // 해당 주문의 대기 결제를 생성
        Payment payment = paymentService.createPayment(order);

        //주문 생성 결과를 반환 장바구니는 결제 성공까지 유지
        return CreateOrderResponse.from(order, payment);
    }

    // 내 주문 목록 조회
    @Transactional(readOnly = true)
    public Page<GetAllOrderResponse> getOrdersAll(Long memberId, Pageable pageable) {
       return orderService.findOrdersByMemberId(memberId, pageable)
                .map(order -> {
                    // PaymentService에서 주문 ID로 Payment 객체 조회하기
                  Optional<Payment> payment = paymentService.findPaymentByOrderId(order.getId());
                  Long paymentId = payment.map(Payment::getId).orElse(null);
                    return GetAllOrderResponse.from(order,paymentId);
                });
    }

    // 주문 상세 조회
    @Transactional(readOnly = true)
    public GetOrderResponse getOrderOne(Long memberId, Long orderId) {
        Order order = orderService.findOrderById(orderId);
        order.validateOwner(memberId);
        Payment payment = paymentService.findPaymentByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return GetOrderResponse.from(order, payment.getId(), payment.getStatus().name());
    }

    // 주문 취소
    public CancelOrderResponse cancelOrder(Long memberId, Long orderId) {
        Order order = orderService.findOrderById(orderId);
        order.validateOwner(memberId);
        Payment payment = paymentService.findPaymentByOrderId(orderId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.PAYMENT_NOT_FOUND)
                );

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.restoreStock(orderItem.getQuantity());
        }

        payment.cancel();
        order.cancel();
        return new CancelOrderResponse(
                order,
                payment.getStatus()
        );
    }
}
