package com.hybe.larva.entity.product_key;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductKeyRepo extends MongoRepository<ProductKey, String> {

    Optional<ProductKey> findByCode(String code);

    Optional<ProductKey> findByCodeAndDeletedIsFalse(String code);

}
