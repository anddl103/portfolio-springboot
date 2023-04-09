package com.hybe.larva.entity.product_key_history;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductKeyHistoryRepo extends MongoRepository<ProductKeyHistory, String> {
}
