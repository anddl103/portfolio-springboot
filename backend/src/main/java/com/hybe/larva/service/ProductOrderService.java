package com.hybe.larva.service;

import com.hybe.larva.dto.product_order.ProductOrderAddRequest;
import com.hybe.larva.dto.product_order.ProductOrderResponse;
import com.hybe.larva.dto.product_order.ProductOrderSearchRequest;
import com.hybe.larva.dto.product_order.ProductOrderUpdateRequest;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.nfc_card.NfcCardRepoExt;
import com.hybe.larva.entity.product_order.ProductOrder;
import com.hybe.larva.entity.product_order.ProductOrderAggregation;
import com.hybe.larva.entity.product_order.ProductOrderRepoExt;
import com.hybe.larva.util.CacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductOrderService {

    private final ProductOrderRepoExt productOrderRepoExt;
    private final NfcCardRepoExt nfcCardRepoExt;
    private final CacheUtil cacheUtil;

    public ProductOrderResponse addProductOrder(ProductOrderAddRequest request) {
        ProductOrder productOrder = request.toEntity();
        productOrder = productOrderRepoExt.insert(productOrder);
        NfcCard nfcCard = nfcCardRepoExt.findById(productOrder.getNfcCardId()).afterOrder();
        nfcCardRepoExt.save(nfcCard);
        return getProductOrder(productOrder.getId());
    }

    public Page<ProductOrderResponse> searchProductOrder(ProductOrderSearchRequest request) {
        final Criteria defaultCriteria = Criteria.where(ProductOrder.DELETED).ne(true);

        if (request.getFrom() != null && request.getTo() != null) {
            defaultCriteria.and(ProductOrder.CREATED_AT).gte(request.getFrom()).lt(request.getTo());
        }

        if (request.getProductOrderStatus() != null) {
            defaultCriteria.and(ProductOrder.STATE).is(request.getProductOrderStatus());
        }

        final Criteria criteria = new Criteria();

        if (request.getArtistId() != null) {
            criteria.and(ProductOrder.ARTIST_ID).is(request.getArtistId());
        }

        return productOrderRepoExt.search(defaultCriteria, criteria, request.getPageable())
                .map(p -> new ProductOrderResponse(p, cacheUtil));
    }

    public ProductOrderResponse getProductOrder(String productOrderId) {
        ProductOrderAggregation productOrderAggregation = productOrderRepoExt.findByIdDetail(productOrderId);
        return new ProductOrderResponse(productOrderAggregation, cacheUtil);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public ProductOrderResponse updateProductOrder(String productOrderId, ProductOrderUpdateRequest request) {
        ProductOrder productOrder = productOrderRepoExt.findById(productOrderId).update(request);
        productOrderRepoExt.save(productOrder);
        return getProductOrder(productOrderId);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public void deleteProductOrder(String productOrderId) {
//        productOrderRepoExt.deleteById(productOrderId);
        ProductOrder productOrder = productOrderRepoExt.findById(productOrderId).delete();
        productOrderRepoExt.save(productOrder);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public ProductOrderResponse completeProductOrder(String productOrderId) {
        ProductOrder productOrder = productOrderRepoExt.findById(productOrderId).complete();
        productOrderRepoExt.save(productOrder);
        return getProductOrder(productOrderId);
    }

    @Retryable(OptimisticLockingFailureException.class)
    public ProductOrderResponse cancelledProductOrder(String productOrderId) {
        ProductOrder productOrder = productOrderRepoExt.findById(productOrderId).cancelled();
        productOrderRepoExt.save(productOrder);
        return getProductOrder(productOrderId);
    }
}
