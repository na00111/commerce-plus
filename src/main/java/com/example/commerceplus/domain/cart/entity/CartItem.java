package com.example.commerceplus.domain.cart.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cart_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class CartItem extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // 필요한 값만 명시해 CartItem을 만들 수 있도록 빌더 생성 지점을 이 생성자로 제한
    @Builder
    private CartItem(Cart cart, Product product, int quantity) {
        // 어느 장바구니에 들어가는 항목인지 저장
        this.cart = cart;
        // 어떤 상품을 담은 항목인지 저장
        this.product = product;
        if (quantity < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        this.quantity = quantity;
    }

    // Cart -> Member 객체 전체를 외부에 노출하지 않고 소유 회원의 id만 반환하는 편의 메서드
    public Long getMemberId() {
        // cart의 member를 따라가 회원 id를 호출한 곳으로 반환 LAZY 상태라면 이 순간 조회가 발생
        return cart.getMember().getId();
    }

    public Long getProductId() {
        return product.getId();
    }

    // 이미 담긴 상품을 다시 담을 때 기존 수량에 추가할 수 있는 도메인 메서드
    public void addQuantity(int quantity) {
        if (quantity < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        this.quantity += quantity;
    }

    // 수량 수정 요청처럼 기존 수량을 새 값으로 교체할 때 사용하는 도메인 메서드
    public void changeQuantity(int quantity) {
        if (quantity < 1) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }
        this.quantity = quantity;
    }
}
