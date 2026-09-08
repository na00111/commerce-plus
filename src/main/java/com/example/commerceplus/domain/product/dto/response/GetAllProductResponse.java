package com.example.commerceplus.domain.product.dto.response;

import com.example.commerceplus.domain.product.entity.Product;

public record GetAllProductResponse(
        Long id,
        String name,
        int price,
        String category,
        String categoryDescription
)
{
    public static GetAllProductResponse from(Product product) {
        return new GetAllProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory().name(),
                product.getCategory().getDescription()
        );
    }
}
