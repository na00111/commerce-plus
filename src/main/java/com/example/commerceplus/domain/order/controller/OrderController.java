package com.example.commerceplus.domain.order.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.api.PageResponse;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.order.dto.request.CreateOrderRequest;
import com.example.commerceplus.domain.order.dto.response.CreateOrderResponse;
import com.example.commerceplus.domain.order.dto.response.GetCheckoutResponse;
import com.example.commerceplus.domain.order.dto.response.GetOrderResponse;
import com.example.commerceplus.domain.order.service.OrderFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    // 내 주문 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GetOrderResponse>>> getOrdersAll(
            @Auth JwtUser jwtUser,
            Pageable pageable
    ) {
        Long memberId = jwtUser.id();
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getOrdersAll(memberId, pageable)));
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<GetOrderResponse>> getOrderOne(
            @Auth JwtUser jwtUser,
            @PathVariable Long orderId
    ) {
        Long memberId = jwtUser.id();
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getOrderOne(memberId, orderId)));
    }

    // 주문 취소


}


