package com.example.commerceplus.domain.payment.service;

import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.payment.entity.Payment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    public static Payment createPayment(Order order, int totalPrice) {
        return null;
    }

    public Optional<Payment> findPaymentByOrderId(Long id) {
        return null;
    }
}
