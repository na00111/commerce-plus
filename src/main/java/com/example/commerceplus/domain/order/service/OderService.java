package com.example.commerceplus.domain.order.service;

import com.example.commerceplus.domain.cart.repository.CartRepository;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.order.dto.OrderCreateRequest;
import com.example.commerceplus.domain.order.dto.OrderCreateResponse;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderItem;
import com.example.commerceplus.domain.order.repository.OrderRepository;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;


    // 주문 생성
    @Transactional
    public OrderCreateResponse createOrder(Member member, OrderCreateRequest requestDto) {

        // 요청받은 상품 ID 목록 추출 (비어있으면 장바구니 전체 사용). requestDto에서 추출
        List<Long> productIds = null;

        // 해당 상품들을 DB에서 조회, productRepository로 조회
        List<Product> products = null;

        // 각 상품의 재고 검증 (재고 부족 시 예외 발생 → 트랜잭션 롤백)
        // 이 부분에서 예외가 터지면 이후 모든 작업이 자동으로 롤백
        validateStock(products);

        // OrderItem 리스트 생성 (스냅샷 저장: 현재 상품명·가격 포함)
        List<OrderItem> orderItems = createOrderItems(products);

        // 재고 차감 (각 상품별로)
        decreaseStock(products);

        // 주문번호 채번 (UUID 또는 자신만의 규칙)
        String orderNumber = generateOrderNumber();

        // 총액 계산
        int totalPrice = calculateTotalPrice(orderItems);

        // Order 엔티티 생성 및 저장
        Order order = new Order(member, orderNumber, totalPrice, orderItems);
        Order savedOrder = orderRepository.save(order);

        // 응답 DTO 생성 (주문 ID, 주문번호, 총액)
        return new OrderCreateResponse(savedOrder);
    }


    // 각 상품의 재고가 충분한지 확인
    // 부족하면 new IllegalArgumentException("상품명: 재고 부족") 등으로 예외 발생
    private void validateStock(List<Product> products) {
    }


    // OrderItem 생성 시 현재 상품명·가격을 스냅샷으로 저장
    private List<OrderItem> createOrderItems(List<Product> products) {
        return null;
    }


    // 각 상품의 재고를 차감
    private void decreaseStock(List<Product> products) {
    }


    // 주문번호 생성 로직
    private String generateOrderNumber() {
        return null;
    }


    // OrderItem들의 가격을 모두 합산
    private int calculateTotalPrice(List<OrderItem> orderItems) {
        return 0;
    }
}
