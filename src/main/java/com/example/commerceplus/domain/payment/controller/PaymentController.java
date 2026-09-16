package com.example.commerceplus.domain.payment.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.api.PageResponse;
import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.payment.dto.request.PostPaymentMockRequest;
import com.example.commerceplus.domain.payment.dto.request.PostPaymentRequest;
import com.example.commerceplus.domain.payment.dto.response.PaymentResponse;
import com.example.commerceplus.domain.payment.service.PaymentFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @PostMapping("/mock/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPaymentMock(
            @Auth JwtUser jwtUser,
            @RequestBody @Valid PostPaymentMockRequest request
    ) {
        // 로그인 사용자 ID와 요청을 Facade로 전달
       PaymentResponse response =  paymentFacade.confirmMock(jwtUser.id(), request);
       return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @Auth JwtUser jwtUser,
            @RequestBody @Valid PostPaymentRequest request
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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPayments(
            @Auth JwtUser jwtUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")  int size
    ) {
        // PageRequest 생성 전에 잘못된 값을 검사
        // 페이지 크기 상한 100
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "페이지는 0 이상 ,사이즈는 1~100으로");
        }
           Pageable pageable = PageRequest.of(page , size);
           Page<PaymentResponse> payments =  paymentFacade.getPayments(jwtUser.id(),pageable);
           PageResponse<PaymentResponse> response = PageResponse.of(payments ,payment -> payment);
           return ResponseEntity.ok(ApiResponse.ok(response));
        }
    }

