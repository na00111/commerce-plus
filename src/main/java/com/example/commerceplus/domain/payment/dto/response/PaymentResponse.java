package com.example.commerceplus.domain.payment.dto.response;

import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PaymentResponse(
    Long paymentId,
    Long orderId,
    int amount,

    @JsonProperty("pay_status")
    String payStatus,
    @JsonProperty("order_status")
    String orderStatus,
    @JsonProperty("paid_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime paidAt
) {
public static PaymentResponse of(Payment payment , Order order) {
    return new PaymentResponse (
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getStatus().name(),
            order.getStatus().name(),
            payment.getPaidAt()
    );
}
}