package com.hybe.larva.entity.message;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageRepoExt {

    private final MessageRepo repo;
    private final MongoOperations mongo;

    public Message insert(Message message) {  return repo.insert(message);  }

    public void deleteById(String id) {  repo.deleteById(id);  }

    public void delete(Message message) {  repo.delete(message);  }

    public Message save(Message message) {  return repo.save(message);  }
}
