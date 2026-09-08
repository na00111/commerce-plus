package com.example.commerceplus.domain.product.service;

import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.product.dto.condition.SearchProductCondition;
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

}
