package com.example.commerceplus.domain.payment.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.payment.dto.request.PaymentRequest;
import com.example.commerceplus.domain.payment.dto.response.PaymentResponse;
import com.example.commerceplus.domain.payment.service.PaymentFacade;
import com.example.commerceplus.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @PostMapping("/mock/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @Auth JwtUser jwtUser,
            @RequestBody PaymentRequest request
    ) {
        // 로그인 사용자 ID와 요청을 Facade로 전달
       PaymentResponse response =  paymentFacade.confirm(jwtUser.id(), request);
       return ResponseEntity.ok(ApiResponse.ok(response));
    }
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @Auth JwtUser jwtUser,
            @PathVariable("paymentId") Long paymentId
    ) {
       PaymentResponse response = paymentFacade.getPayment(jwtUser.id(), paymentId);
       return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
