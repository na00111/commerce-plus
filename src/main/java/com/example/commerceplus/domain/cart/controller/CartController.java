package com.example.commerceplus.domain.cart.controller;

import com.example.commerceplus.common.annotation.Auth;
import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.jwt.JwtUser;
import com.example.commerceplus.domain.cart.dto.request.AddCartItemRequest;
import com.example.commerceplus.domain.cart.dto.request.UpdateCartItemQuantityRequest;
import com.example.commerceplus.domain.cart.dto.response.AddCartItemResponse;
import com.example.commerceplus.domain.cart.dto.response.CartResponse;
import com.example.commerceplus.domain.cart.dto.response.UpdateCartItemQuantityResponse;
import com.example.commerceplus.domain.cart.facade.CartFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartFacade cartFacade;

@PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<AddCartItemResponse>> addItem(
      @Auth JwtUser user,
        @PathVariable Long productId,
        @RequestBody @Valid AddCartItemRequest request
    ) {
    int finalQuantity = cartFacade.addItem(user.id(),productId, request.quantity());
    AddCartItemResponse response = AddCartItemResponse.of(finalQuantity);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@Auth JwtUser user) {
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(cartFacade.getCart(user.id())));
    }
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<UpdateCartItemQuantityResponse>>  updateItemQuantity(
            @Auth JwtUser user,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemQuantityRequest request
            ) {
    int updatedQuantity = cartFacade.UpdateCartItemQuantity(user.id(), cartItemId, request.quantity());
    UpdateCartItemQuantityResponse response = UpdateCartItemQuantityResponse.of(updatedQuantity);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(response));
    }
}
