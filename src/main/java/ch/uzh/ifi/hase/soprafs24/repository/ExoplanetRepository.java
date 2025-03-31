package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("exoplanetRepository")
public interface ExoplanetRepository extends MongoRepository<Exoplanet, String> {
    Exoplanet findByPlanetName(String planetName);
    List<Exoplanet> findAllByOrderByMassAsc();
    List<Exoplanet> findAllByOrderByMassDesc();
    List<Exoplanet> findAllByOrderByRadiusAsc();
    List<Exoplanet> findAllByOrderByRadiusDesc();
    List<Exoplanet> findAllByOrderByTheoreticalTemperatureAsc();
    List<Exoplanet> findAllByOrderByTheoreticalTemperatureDesc();
    List<Exoplanet> findAllByOrderByDensityAsc();
    List<Exoplanet> findAllByOrderByDensityDesc();
    List<Exoplanet> findAllByOrderByEarthSimilarityIndexAsc();
    List<Exoplanet> findAllByOrderByEarthSimilarityIndexDesc();
}
