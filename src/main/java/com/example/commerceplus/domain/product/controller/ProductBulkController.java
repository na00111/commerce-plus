package com.example.commerceplus.domain.product.controller;

import com.example.commerceplus.domain.product.service.ProductBulkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class ProductBulkController {

    private final ProductBulkService productBulkService;

    @GetMapping("/api/products/bulk")
    public ResponseEntity<Void> bulk() {
        productBulkService.createAndSaveBulkProducts();
        return ResponseEntity.ok().build();
    }

}
