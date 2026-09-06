package com.example.commerceplus.domain.order.repository;

import com.example.commerceplus.domain.order.entity.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

    // 내 주문 목록 조회 (최신순)
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.member.id = :memberId ORDER BY o.createdAt DESC")
    List<Order> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);

    // 주문 단건 상세 조회
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderItems(@Param("orderId") Long orderId);

}
