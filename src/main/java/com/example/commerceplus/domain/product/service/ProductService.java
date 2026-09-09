package com.example.commerceplus.domain.product.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.product.dto.condition.SearchProductCondition;
import com.example.commerceplus.domain.product.dto.request.PatchProductRequest;
import com.example.commerceplus.domain.product.dto.response.GetProductResponse;
import com.example.commerceplus.domain.product.entity.Product;
import com.example.commerceplus.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> findProductAll(Pageable pageable, SearchProductCondition condition) {

        if (condition.isMinPriceGreaterThanMaxPrice()) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }

       return productRepository.findProductsByCondition(pageable, condition);
    }

    @Transactional(readOnly = true)
    public GetProductResponse findProduct(Long productId) {

        boolean isExistsProduct = productRepository.existsById(productId);
        if (!isExistsProduct) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Product product = productRepository.findById(productId).get();
        return GetProductResponse.from(product);
    }

    @Transactional
    public GetProductResponse updateProduct(Long productId, PatchProductRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateProduct(request.name(),  request.price(), request.comment(), request.category());
        productRepository.save(product);
        return GetProductResponse.from(product);
    }

    @Transactional // 동시성 제연을 위해 락이 없는 버전
    public Product findProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional // 동시성을 막기 위한 비관적 락을 사용한 버전
    public Product findProductByIdWithLock(Long productId) {
        return productRepository.findByIdWithLock(productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

}
