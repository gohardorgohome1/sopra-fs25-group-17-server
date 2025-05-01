package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdAndSeenFalse(String userId);
    List<Notification> findByUserIdAndExoplanetId(String userId, String exoplanetId);
    List<Notification> findByUserId(String userId);
}
