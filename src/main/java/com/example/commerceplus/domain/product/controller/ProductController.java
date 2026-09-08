package com.example.commerceplus.domain.product.controller;

import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.common.api.PageResponse;
import com.example.commerceplus.domain.product.dto.condition.SearchProductCondition;
import com.example.commerceplus.domain.product.dto.response.GetAllProductResponse;
import com.example.commerceplus.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<GetAllProductResponse>>> getProductAll(
            @Valid SearchProductCondition condition )

    {

        Pageable pageable = PageRequest.of(condition.page(), condition.size());
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.of(
                productService.findProductAll(pageable, condition),
                GetAllProductResponse::from
        )));
    }

}
