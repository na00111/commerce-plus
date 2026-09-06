package com.example.commerceplus.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false,length = 200)
    private String productName;

    @Column(name = "price_snapshot", nullable = false, columnDefinition = "int UNSIGNED")
    private int priceSnapshot;

    @Column(nullable = false, columnDefinition = "int UNSIGNED")
    private int quantity;



    /*public OrderItem(Product product, int priceSnapshot, int quantity) {
        this.product = product;
        this.productName = product.getName();
        this.priceSnapshot = priceSnapshot;
        this.quantity = quantity;
    }

    void setOrder(Order order) {
        this.order = order;
    }*/



    public int getSubtotal() {
        return priceSnapshot * quantity;
    }
}
