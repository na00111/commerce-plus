package com.example.commerceplus.domain.payment.repository;

import com.example.commerceplus.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 모의 결제 처리 시 주문 ID로 결제 조회
    Optional<Payment> findByOrder_Id(Long orderId);

    // 결제 단건 조회
    // 응답에서 주문 상태를 사용하므로 Order를 함께 조회
    @Override
    @EntityGraph(attributePaths = "order")
    Optional<Payment> findById(Long paymentId);

    // 로그인 회원의 결제 목록을 최신순으로 조회
    // 각 결제의 주문 상태를 사용할 때 N+1이 발생하지 않도록 Order를 함께 조회
    @EntityGraph(attributePaths = "order")
    Page<Payment> findAllByMember_IdOrderByCreatedAtDesc(
            Long memberId,
            Pageable pageable
    );

    // 한 주문에 결제가 중복 생성되는 것을 사전에 확인
    boolean existsByOrder_Id(Long orderId);
}