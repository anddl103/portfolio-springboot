package com.hybe.larva.entity.faq;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FaqRepo extends MongoRepository<Faq, String> {

//    List<Faq> findAllByCategoryAndLocaleCode(String category, String localeCode);
}
