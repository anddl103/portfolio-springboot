package com.hybe.larva.service;

import com.hybe.larva.dto.product_key_history.ProductKeyHistoryAddRequest;
import com.hybe.larva.dto.product_key_history.ProductKeyHistoryResponse;
import com.hybe.larva.entity.product_key.ProductKey;
import com.hybe.larva.entity.product_key_history.ProductKeyHistory;
import com.hybe.larva.entity.product_key_history.ProductKeyHistoryRepoExt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductKeyHistoryService {

    private final ProductKeyHistoryRepoExt productKeyHistoryRepoExt;

    public ProductKeyHistoryResponse addProductKeyHistory(String diviceInfo, String failedReason, ProductKey productKey) {

        ProductKeyHistory productKeyHistory = ProductKeyHistoryAddRequest.builder()
                .deviceInfo(diviceInfo).failedReason(failedReason).build()
                    .toEntity(productKey);

        productKeyHistory = productKeyHistoryRepoExt.insert(productKeyHistory);
        return new ProductKeyHistoryResponse(productKeyHistory);
    }
}
