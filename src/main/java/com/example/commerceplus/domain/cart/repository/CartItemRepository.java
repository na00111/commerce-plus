package com.example.commerceplus.domain.cart.repository;


import com.example.commerceplus.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 회원의 모든 CartItem을 조회하면서 각 상품도 한 SQL로 함께 가져와 N+1 문제를 줄임
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.cart.member.id = :memberId")
    List<CartItem> findAllByMemberIdWithProduct(@Param("memberId") Long memberId);

    // 메서드 이름을 분석해 cart.member.id와 product.id가 모두 일치하는 항목 한 개를 조회
    Optional<CartItem> findByCart_Member_IdAndProduct_Id(Long memberId, Long productId);

    @Modifying
    // 항목 id와 회원 id를 동시에 검사하므로 다른 회원의 장바구니 항목을 id만으로 삭제하지 못하게 함.
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id AND ci.cart.member.id = :memberId")
    int deleteByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

    @Modifying
    // 해당 회원 장바구니의 모든 항목을 삭제
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId")
    int deleteAllByMemberId(@Param("memberId") Long memberId);

    // 선택한 항목만 조회하되 Product도 fetch join하여 상품명·가격 접근 시 추가 SQL을 줄임
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.id IN :ids AND ci.cart.member.id = :memberId")
    List<CartItem> findByIdInAndMemberIdWithProduct(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

    @Modifying
    // CartItem id 목록과 회원 id를 함께 검사해 해당 회원 소유의 선택 항목만 삭제
    @Query("DELETE FROM CartItem c WHERE c.id IN :ids AND c.cart.member.id = :memberId")
    // 실제 삭제된 행 수를 반환하므로 요청 개수와 비교해 누락 또는 소유권 불일치를 확인할 수 있다.
    int deleteAllByIdInAndMemberId(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

    // 실행 전 변경을 DB에 반영하고 실행 후 영속성 컨텍스트를 비워 벌크 삭제 뒤의 오래된 객체 상태를 방지합니다.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    // CartItem id가 아니라 Product id 목록을 기준으로 해당 회원의 장바구니 항목을 삭제합니다.
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId AND ci.product.id IN :productIds")
    // 실제 삭제된 행 수를 반환합니다. 이 메서드도 쓰기 트랜잭션 안에서 호출해야 합니다.
    int deleteAllByMemberIdAndProductIdIn(@Param("memberId") Long memberId, @Param("productIds") List<Long> productIds);
}
