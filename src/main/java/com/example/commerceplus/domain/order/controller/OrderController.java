package com.example.commerceplus.domain.order.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.order.dto.request.CreateOrderRequest;
import com.example.commerceplus.domain.order.dto.response.CreateOrderResponse;
import com.example.commerceplus.domain.order.dto.response.GetCheckoutResponse;
import com.example.commerceplus.domain.order.service.OrderFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    // 주문서 미리보기
    @GetMapping("/checkout")
    public ResponseEntity<ApiResponse<GetCheckoutResponse>> getCheckoutOne(
            @Auth JwtUser jwtUser,
            @RequestParam(required = true) @NotEmpty List<Long> cartItemIds
    ) {
        Long memberId = jwtUser.id();
        return ResponseEntity.ok(ApiResponse.ok(
                orderFacade.getCheckoutOne(memberId, cartItemIds == null ? List.of() : cartItemIds)
        ));
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Auth JwtUser jwtUser,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        Long memberId = jwtUser.id();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(orderFacade.createOrder(memberId, request)));
    }

}


