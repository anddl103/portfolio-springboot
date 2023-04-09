package com.hybe.larva.entity.product_key;

import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.entity.product_order.ProductOrder;
import com.hybe.larva.exception.InvalidProductKeyException;
import com.hybe.larva.exception.ResourceNotFoundException;
import com.hybe.larva.exception.TaggedProductKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductKeyRepoExt {

    private final ProductKeyRepo repo;
    private final MongoOperations mongo;

    public ProductKey insert(ProductKey productKey) {
        return repo.insert(productKey);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    public void delete(ProductKey productKey) {
        repo.delete(productKey);
    }

    public ProductKey save(ProductKey productKey) {
        return repo.save(productKey);
    }

    public ProductKey findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find  ProductKey: id=" + id));
    }

    public ProductKey findByCode(String code) throws ResourceNotFoundException {
        return repo.findByCodeAndDeletedIsFalse(code).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find ProductKey: code=" + code));
    }

    public Page<ProductKey> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<ProductKey> productKeys = mongo.find(query, ProductKey.class);
        return PageableExecutionUtils.getPage(productKeys, pageable,
                () -> mongo.count(new Query(criteria), ProductKey.class)
        );
    }

    public ProductKey verifyByCode(String code) {
        ProductKey productKey = findByCode(code);

        // validate flags
        if (!Boolean.TRUE.equals(productKey.isAssigned())
                || StringUtils.isEmpty(productKey.getProductOrderId())
                || StringUtils.isEmpty(productKey.getNfcCardId())) {
            throw new InvalidProductKeyException("This is an unassigned code: " + this);
        } else if (Boolean.TRUE.equals(productKey.isTagged())) {
            // 사용자가 tag 한 코드
            throw new TaggedProductKeyException("This code is already tagged: " + this);
        }
//        else if (Boolean.TRUE.equals(productKey.isVerified())) {
//            throw new VerifiedProductKeyException("This is already verified code: " + this);
//        }

        // check reference keys
        ProductOrder order = mongo.findById(productKey.getProductOrderId(), ProductOrder.class);
        NfcCard nfcCard = mongo.findById(productKey.getNfcCardId(), NfcCard.class);
        if (order == null || nfcCard == null || !nfcCard.getId().equals(order.getNfcCardId())) {
            throw new InvalidProductKeyException("Invalid reference keys (please check order): " + productKey);
        }

        return productKey;
    }

    public ProductKey assignByCode(String code) {
//        ProductKey productKey = findByCode(code);
        Query query = Query.query(Criteria.where(ProductKey.CODE).is(code)
                .and(ProductKey.ASSIGNED).ne(true)
                .and(ProductKey.VERIFIED).ne(true)
                .and(ProductKey.TAGGED).ne(true));
        ProductKey productKey = mongo.findOne(query, ProductKey.class);

        // validate flags
        if (productKey == null) {
            throw new ResourceNotFoundException("Non-existent product key code: " + this);
        }

        return productKey;
    }

    public long findByProductOderVerify(String productOrderId) {

        Query query = new Query(Criteria.where(ProductKey.PRODUCT_ORDER_ID).is(productOrderId)
                .and(ProductKey.DELETED).ne(true)
                .and(ProductKey.VERIFIED).is(true));
        return mongo.count(query, ProductKey.class);
    }


}
