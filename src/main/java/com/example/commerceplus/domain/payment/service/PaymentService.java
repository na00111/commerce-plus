package com.example.commerceplus.domain.payment.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.order.entity.Order;
import com.example.commerceplus.domain.payment.entity.Payment;
import com.example.commerceplus.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public static Payment createPayment(Order order, int totalPrice) {
        return null;
    }

    public Payment findByOrderIdWithOrder(Long orderId) {
        return paymentRepository.findByOrderIdWithOrder(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    public Payment findPaymentByOrderId(Long orderId) {
        return findByOrderIdWithOrder(orderId);
    }
}
