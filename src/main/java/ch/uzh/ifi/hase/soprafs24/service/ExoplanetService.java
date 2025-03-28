package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Exoplanet Service
 * This class is responsible for all functionality related to the exoplanet
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class ExoplanetService {

    private final Logger log = LoggerFactory.getLogger(ExoplanetService.class);

    private final ExoplanetRepository exoplanetRepository;

    @Autowired
    public ExoplanetService(@Qualifier("exoplanetRepository") ExoplanetRepository exoplanetRepository) {
        this.exoplanetRepository = exoplanetRepository;
    }

    /**
     * Get all Exoplanets
     * @return List of all Exoplanets from the database
     */
    public List<Exoplanet> getExoplanets() {
        return this.exoplanetRepository.findAll();
    }

    /**
     * Get Exoplanet by its ID
     * @param id ID of the Exoplanet
     * @return Exoplanet entity
     * @throws ResponseStatusException if Exoplanet is not found
     */
    public Exoplanet getExoplanetById(Long id) {
        return exoplanetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found"));
    }

    /**
     * Create a new Exoplanet
     * @param newExoplanet Exoplanet entity to be created
     * @return The created Exoplanet entity
     */
    public Exoplanet createExoplanet(Exoplanet newExoplanet) {
        // You can add logic here, e.g., validate or enrich the Exoplanet data.
        newExoplanet = exoplanetRepository.save(newExoplanet);
        exoplanetRepository.flush();
        log.debug("Created Information for Exoplanet: {}", newExoplanet);
        return newExoplanet;
    }

    /**
     * Delete an Exoplanet by ID
     * @param id ID of the Exoplanet to delete
     * @throws ResponseStatusException if Exoplanet is not found
     */
    public void deleteExoplanet(Long id) {
        Exoplanet exoplanet = exoplanetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found"));
        exoplanetRepository.delete(exoplanet);
        log.debug("Deleted Exoplanet with ID: {}", id);
    }

    public List<Exoplanet> getExoplanetRanking(String sortBy, String order) {
        // Determine sorting order
        boolean ascending = order.equals("asc");
    
        // Depending on the "sortBy" parameter, construct the query accordingly
        List<Exoplanet> exoplanets;
        switch (sortBy.toLowerCase()) {
            case "mass":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderByMassAsc() : exoplanetRepository.findAllByOrderByMassDesc();
                break;
            case "size":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderBySizeAsc() : exoplanetRepository.findAllByOrderBySizeDesc();
                break;
            case "radius":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderByRadiusAsc() : exoplanetRepository.findAllByOrderByRadiusDesc();
                break;
            case "temperature":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderByTemperatureAsc() : exoplanetRepository.findAllByOrderByTemperatureDesc();
                break;
            case "density":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderByDensityAsc() : exoplanetRepository.findAllByOrderByDensityDesc();
                break;
            case "esi":
                exoplanets = ascending ? exoplanetRepository.findAllByOrderByEarthSimilarityIndexAsc() : exoplanetRepository.findAllByOrderByEarthSimilarityIndexDesc();
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sorting criteria.");
        }
    
        return exoplanets;
    }
    



}

