package com.hybe.larva.entity.product_order;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductOrderRepo extends MongoRepository<ProductOrder, String> {

}
