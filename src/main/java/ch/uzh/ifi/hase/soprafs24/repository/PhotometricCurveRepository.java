package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotometricCurveRepository extends MongoRepository<PhotometricCurve, String> {

    Optional<PhotometricCurve> findById(String id);

}