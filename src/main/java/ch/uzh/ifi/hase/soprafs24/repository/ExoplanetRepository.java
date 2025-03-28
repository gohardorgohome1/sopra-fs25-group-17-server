package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("exoplanetRepository")
public interface ExoplanetRepository extends JpaRepository<Exoplanet, Long> {
  
    // Find an exoplanet by its name
    Exoplanet findByPlanetName(String planetName);
  }