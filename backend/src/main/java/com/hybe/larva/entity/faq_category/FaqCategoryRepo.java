package com.hybe.larva.entity.faq_category;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FaqCategoryRepo extends MongoRepository<FaqCategory, String> {
    
}
