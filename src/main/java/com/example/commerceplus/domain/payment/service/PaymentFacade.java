package com.example.commerceplus.domain.payment.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.cart.service.CartItemService;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderItem;
import com.example.commerceplus.domain.order.service.OrderService;
import com.example.commerceplus.domain.payment.dto.request.PaymentRequest;
import com.example.commerceplus.domain.payment.dto.response.PaymentResponse;
import com.example.commerceplus.domain.payment.entity.Payment;
import com.example.commerceplus.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
//결제 처리 순서 , 전체 트랜잭션
public class PaymentFacade {
    private final OrderService orderService;
    private final PaymentService  paymentService;
    private final CartItemService cartItemService;
    private final ProductService productService;

    public PaymentResponse confirm(
            Long memberId, PaymentRequest request
    ) {
       validateRequest(request);
        //주문 먼저 잠그기
        Order order = orderService.findOderIdWithLock(request.orderId());
        //조회한 주문이 로그인한 회원의 주문인지 확인 소유자 검사
        order.validateOwner(memberId);
        //잠금 주문 확보한 후 기존 결제 조ㅚ
        Payment payment = paymentService.findByOrderIdWithOrder(order.getId());
        //결제 주문, 상태와 서버 걀제 금액 검증
        validatePayment(request,order,payment);
        //enum -> 성공 아니면 실패
        switch (request.result()) {
            case SUCCESS -> completePayment(memberId,payment,order);
            case FAIL -> failPayment(payment,order);
        }
        //변경된 값을 응답 객체로 변환
        return PaymentResponse.from(payment);
    }

    public void validatePayment(PaymentRequest request,Order order, Payment payment) {
        //결제 상태 검사
        payment.validatePendingPayment();
        //주문 상태 검사
        order.validatePaymentPending();
        //금액 검사
        payment.validateAmount(request.amount());

    }

    private void completePayment(
            Long memberId,
            Payment payment,
            Order order
    ) {
        // 이번 주문에 포함된 상품 ID를 가져옴
        List<Long> productIds = getOrderedProductIds(order);

        // 결제를 완료하고 완료 시각을 기록
        payment.complete(LocalDateTime.now());

        // 주문 상태를 완료로 변경
        order.completePayment();

        // 해당 회원의 장바구니에서 주문한 상품만 삭제
        cartItemService.deleteOrderedProducts(memberId, productIds);


    }

    private void failPayment(Payment payment, Order order) {
        // 복구할 상품과 수량을 주문 기록에서 추출
        Map<Long,Integer> quantities = getRestoreQuantities(order);
        // 실패 결과를 저장할 상태로 변경
        payment.fail();
        order.cancel();
        // 복구 중 예외가 발생하면  상태 변경도 함께 롤백
        productService.restoreStocks(quantities);
}

    private List<Long> getOrderedProductIds(Order order) {
        return order.getOrderItems().stream()
                .map(item ->item.getProduct().getId())
                .distinct()
                .toList();
    }

    private Map<Long,Integer> getRestoreQuantities(Order order) {
        Map<Long,Integer> quantities = new TreeMap<>();
        for (OrderItem item : order.getOrderItems()) {
            Long productId = item.getProduct().getId();
            int quantity = item.getQuantity();
            // 같은 상품이 여러 항목에 있으면 수량을 합침
            quantities.merge(productId, quantity, Math::addExact);
        }
        return quantities;
}

    private void validateRequest(PaymentRequest request) {
        if (request == null
            || request.orderId() == null      // 1. 주문 ID 값이 아예 비어있거나(null)
            || request.orderId() < 1         // 2. 또는, 주문 ID가 0 이하의 잘못된 숫자이거나
            || request.result() == null       // 3. 또는, 처리 결과(result) 값이 비어있거나(null)
            || request.amount() == null       // 4. 또는, 결제 금액(amount) 값이 비어있거나(null)
            || request.amount() < 1 )          // 5. 또는, 결제 금액이 1원 미만(0원 이하)인 경우
        {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
}
    public PaymentResponse getPayment(Long memberId,Long paymentId) {
    // 결제 ID로 결제와 연결된 주문을 함께 조회
    Payment payment =  paymentService.findByIdWithOrder(paymentId);
    // 결제 조회에서도 반드시 소유자를 검사
   payment.getOrder().validateOwner(memberId);
    return PaymentResponse.from(payment);

}
}
