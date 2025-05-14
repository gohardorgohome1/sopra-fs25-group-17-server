package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChatGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatGroupRepository extends MongoRepository<ChatGroup, String> {
    List<ChatGroup> findByUserIdsContaining(String userId);
}


