package com.example.commerceplus.domain.cart.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.cart.dto.request.AddCartItemRequest;
import com.example.commerceplus.domain.cart.dto.response.AddCartIResponse;
import com.example.commerceplus.domain.cart.facade.CartFacade;
import com.example.commerceplus.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    private final CartFacade cartFacade;

@PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> addItem(
      @AuthenticationPrincipal Long memberId,
        @PathVariable Long productId,
        @RequestBody @Valid AddCartItemRequest request
    ) {
    cartFacade.addItem(memberId,productId, request.quantity());
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok());
    }
}
