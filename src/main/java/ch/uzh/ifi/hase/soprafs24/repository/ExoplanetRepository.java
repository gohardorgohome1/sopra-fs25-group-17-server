package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("exoplanetRepository")
public interface ExoplanetRepository extends JpaRepository<Exoplanet, Long> {
  
    // Find an exoplanet by its name
    Exoplanet findByPlanetName(String planetName);

    List<Exoplanet> findAllByOrderByMassAsc();
    List<Exoplanet> findAllByOrderByMassDesc();

    List<Exoplanet> findAllByOrderBySizeAsc();
    List<Exoplanet> findAllByOrderBySizeDesc();

    List<Exoplanet> findAllByOrderByRadiusAsc();
    List<Exoplanet> findAllByOrderByRadiusDesc();

    List<Exoplanet> findAllByOrderByTemperatureAsc();
    List<Exoplanet> findAllByOrderByTemperatureDesc();

    List<Exoplanet> findAllByOrderByDensityAsc();
    List<Exoplanet> findAllByOrderByDensityDesc();

    List<Exoplanet> findAllByOrderByEarthSimilarityIndexAsc();
    List<Exoplanet> findAllByOrderByEarthSimilarityIndexDesc();
  }