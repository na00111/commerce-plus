package com.example.commerceplus.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oder_items")
@Getter
@NoArgsConstructor
public class OderItem {

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

    @Column(name = "order_price", nullable = false, columnDefinition = "int UNSIGNED")
    private int orderPrice;

    @Column(nullable = false, columnDefinition = "int UNSIGNED")
    private int quantity;



    /*public OrderItem(Product product, int orderPrice, int quantity) {
        this.product = product;
        this.productName = product.getName();
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    void setOrder(Order order) {
        this.order = order;
    }*/



    public int getSubtotal() {
        return orderPrice * quantity;
    }
}
