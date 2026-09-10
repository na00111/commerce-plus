package com.example.commerceplus.domain.order.dto.response;

import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.order.entity.OrderStatus;
import com.example.commerceplus.domain.payment.entity.PaymentStatus;
import lombok.Getter;

@Getter
public class CancelOrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus orderStatus;
    private PaymentStatus payStatus;

    public CancelOrderResponse(Order order, PaymentStatus paymentStatus) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.orderStatus = order.getStatus();
        this.payStatus = paymentStatus;
    }
}
