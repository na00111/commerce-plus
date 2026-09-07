package com.example.commerceplus.domain.product.entity;

import com.example.commerceplus.common.entity.BaseTimeEntity;
import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int price;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int stock;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    public Product(String name, int price, int stock, String comment, Category category, Status status) {
        isPriceLessThanZero(price);
        isStockLessThanOne(stock);

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.comment = comment;
        this.category = category;
        this.status = status;
    }

    public void decreaseStock(int stock) {
        isStockLessThanOne(stock);

        if (this.stock - stock < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        this.stock -= stock;
    }

    public void restoreStock(int stock) {
        isStockLessThanOne(stock);
        this.stock += stock;
    }

    public boolean isEnoughStock(int stock) {
        return this.stock >= stock;
    }

    private void isStockLessThanOne(int stock) {
        if (stock < 1) {
            throw new BusinessException(ErrorCode.INVALID_STOCK);
        }
    }

    private void isPriceLessThanZero(int price) {
        if (price < 1) {
            throw new BusinessException(ErrorCode.INVALID_PRICE);
        }
    }

}
