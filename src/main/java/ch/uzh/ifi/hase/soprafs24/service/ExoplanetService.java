package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExoplanetService {

    private final Logger log = LoggerFactory.getLogger(ExoplanetService.class);
    private final ExoplanetRepository exoplanetRepository;

    @Autowired
    public ExoplanetService(@Qualifier("exoplanetRepository") ExoplanetRepository exoplanetRepository) {
        this.exoplanetRepository = exoplanetRepository;
    }

    public List<Exoplanet> getExoplanets() {
        return exoplanetRepository.findAll();
    }

    public Exoplanet getExoplanetById(String id) {
        return exoplanetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found"));
    }

    public void deleteExoplanet(String id) {
        Exoplanet exoplanet = exoplanetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found"));
        exoplanetRepository.delete(exoplanet);
    }

    public List<Exoplanet> getExoplanetRanking(String sortBy, String order) {
        boolean ascending = order.equals("asc");
        return switch (sortBy.toLowerCase()) {
            case "mass" -> ascending ? exoplanetRepository.findAllByOrderByMassAsc() : exoplanetRepository.findAllByOrderByMassDesc();
            case "radius" -> ascending ? exoplanetRepository.findAllByOrderByRadiusAsc() : exoplanetRepository.findAllByOrderByRadiusDesc();
            case "temperature" -> ascending ? exoplanetRepository.findAllByOrderByTheoreticalTemperatureAsc() : exoplanetRepository.findAllByOrderByTheoreticalTemperatureDesc();
            case "density" -> ascending ? exoplanetRepository.findAllByOrderByDensityAsc() : exoplanetRepository.findAllByOrderByDensityDesc();
            case "esi" -> ascending ? exoplanetRepository.findAllByOrderByEarthSimilarityIndexAsc() : exoplanetRepository.findAllByOrderByEarthSimilarityIndexDesc();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sorting criteria.");
        };
    }
}