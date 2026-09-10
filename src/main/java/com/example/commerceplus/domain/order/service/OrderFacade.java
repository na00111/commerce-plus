package com.example.commerceplus.domain.order.service;

import com.example.commerceplus.common.api.PageResponse;
import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.cart.entity.CartItem;
import com.example.commerceplus.domain.cart.service.CartItemService;
import com.example.commerceplus.domain.cart.service.CartService;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.member.sevice.MemberService;
import com.example.commerceplus.domain.order.dto.request.CancelOrderRequest;
import com.example.commerceplus.domain.order.dto.request.CreateOrderRequest;
import com.example.commerceplus.domain.order.dto.response.CancelOrderResponse;
import com.example.commerceplus.domain.order.dto.response.CreateOrderResponse;
import com.example.commerceplus.domain.order.dto.response.GetCheckoutResponse;
import com.example.commerceplus.domain.order.dto.response.GetOrderResponse;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderItem;
import com.example.commerceplus.domain.order.repository.OrderRepository;
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
import java.util.List;

import static com.example.commerceplus.domain.order.entity.QOrder.order;

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
        List<CartItem> cartItems = cartItemService.findAndValidateCartItems(memberId, cartItemIds);
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
        List<CartItem> cartItems = cartItemService.findAndValidateCartItems(memberId, request.cartItemIds());
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

    // 내 주문 목록 조회
    public PageResponse<GetOrderResponse> getOrdersAll(Long memberId, Pageable pageable) {
        Page<GetOrderResponse> orders = orderService.findOrdersByMemberId(memberId, pageable)
                .map(order -> {
                    // PaymentService에서 주문 ID로 Payment 객체 조회하기
                    Payment payment = paymentService.findPaymentByOrderId(order.getId())
                            .orElse(null);
                    return GetOrderResponse.from(order, payment);
                });
        return PageResponse.from(orders);
    }

    // 주문 상세 조회
    public GetOrderResponse getOrderOne(Long memberId, Long orderId) {
        Order order = orderService.findOrderById(orderId);
        validateOrderOwner(order, memberId);
        Long paymentId = paymentService.findPaymentIdByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return GetOrderResponse.from(order, paymentId);
    }

    // 주문 취소
    public CancelOrderResponse cancelOrder(
            Long memberId,
            Long orderId,
            CancelOrderRequest request
    ) {
        Order order = OrderRepository
                .findByIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        // TODO: 취소 가능 상태 검증
        // TODO: 주문 상품 재고 복구
        // TODO: 결제 상태 변경

        order.cancel();

        return CancelOrderResponse.from(order);
    }





    // 주문할 장바구니 상품을 조회하고 유효성 검사
    private List<CartItem> findAndValidateCartItems(Long memberId, List<Long> cartItemIds) {
        List<CartItem> cartItems = cartItemIds.isEmpty()
                ? cartService.findCartEntities(memberId)
                : cartService.findCartEntitiesByIds(memberId, cartItemIds);

        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }
        if (!cartItemIds.isEmpty() && cartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        return cartItems;
    }

    // 주문이 현재 로그인한 회원의 것이지 확인
    private void validateOrderOwner(Order order, Long memberId) {
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
    }


}
