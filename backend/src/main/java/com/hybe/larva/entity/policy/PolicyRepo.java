package com.hybe.larva.entity.policy;

import com.hybe.larva.enums.Usage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PolicyRepo extends MongoRepository<Policy, String> {

    Optional<Policy> findByUsage(Usage usage);

}
