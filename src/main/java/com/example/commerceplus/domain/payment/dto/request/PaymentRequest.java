package com.example.commerceplus.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest(
        @NotNull @Min(1)
        Long orderId,
       MockPaymentResult result,
        @NotNull @Min(1) Integer amount
) {
}
