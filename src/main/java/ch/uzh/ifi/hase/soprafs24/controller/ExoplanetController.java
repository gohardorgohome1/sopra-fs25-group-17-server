package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ExoplanetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Exoplanet Controller
 * This class is responsible for handling all REST requests related to
 * exoplanets.
 * The controller will receive the request and delegate the execution to the
 * ExoplanetService and finally return the result.
 */
@RestController
@RequestMapping("/exoplanets") // Base URL for Exoplanet-related requests
public class ExoplanetController {

    private final ExoplanetService exoplanetService;

    // Constructor Injection of ExoplanetService
    public ExoplanetController(ExoplanetService exoplanetService) {
        this.exoplanetService = exoplanetService;
    }

    /**
     * Get all Exoplanets
     * @return List of Exoplanet DTOs
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ExoplanetGetDTO> getAllExoplanets() {
        List<Exoplanet> exoplanets = exoplanetService.getExoplanets();
        List<ExoplanetGetDTO> exoplanetGetDTOs = new ArrayList<>();

        // Convert each Exoplanet to DTO representation
        for (Exoplanet exoplanet : exoplanets) {
            exoplanetGetDTOs.add(DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(exoplanet));
        }
        return exoplanetGetDTOs;
    }

    /**
     * Get Exoplanet by its ID
     * @param id Exoplanet ID
     * @return Exoplanet details in DTO format
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ExoplanetGetDTO> getExoplanetById(@PathVariable Long id) {
        Exoplanet exoplanet = exoplanetService.getExoplanetById(id);
        ExoplanetGetDTO exoplanetGetDTO = DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(exoplanet);
        return ResponseEntity.ok(exoplanetGetDTO);
    }

    /**
     * Create a new Exoplanet IS NOT NEEDED HERE!
     *  @param exoplanetPostDTO Exoplanet data from the request body
     * @return The created Exoplanet in DTO format
     */
     /*
      * 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ExoplanetGetDTO createExoplanet(@RequestBody ExoplanetPostDTO exoplanetPostDTO) {
        // Convert the DTO to the internal Exoplanet entity
        Exoplanet exoplanetInput = DTOMapper.INSTANCE.convertExoplanetPostDTOtoEntity(exoplanetPostDTO);

        // Create the Exoplanet and save it
        Exoplanet createdExoplanet = exoplanetService.createExoplanet(exoplanetInput);

        // Convert the created Exoplanet back to the DTO
        return DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(createdExoplanet);
    }
      */

    /**
     * Delete an Exoplanet by ID
     * @param id Exoplanet ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void deleteExoplanet(@PathVariable Long id) {
        exoplanetService.deleteExoplanet(id);
    }


    @GetMapping("/ranking")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ExoplanetGetDTO> getExoplanetRanking(
        @RequestParam(value = "sortBy", defaultValue = "esi") String sortBy, 
        @RequestParam(value = "order", defaultValue = "desc") String order) {

        // Validate the sortBy and order parameters
        if (!isValidSortBy(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortBy parameter. Allowed values: esi, mass, size, radius, temperatur, density");
        }
        if (!order.equals("asc") && !order.equals("desc")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order parameter. Allowed values: asc, desc.");
        }

        // Call the service to fetch the ranked Exoplanets
        List<Exoplanet> exoplanets = exoplanetService.getExoplanetRanking(sortBy, order);

        // Convert the list of Exoplanets to DTOs
        List<ExoplanetGetDTO> exoplanetGetDTOs = new ArrayList<>();
        for (Exoplanet exoplanet : exoplanets) {
            exoplanetGetDTOs.add(DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(exoplanet));
        }

        return exoplanetGetDTOs;
    }

    // Helper function to validate the "sortBy" parameter
    private boolean isValidSortBy(String sortBy) {
        return sortBy.equals("mass") || sortBy.equals("size") || sortBy.equals("radius") || 
            sortBy.equals("temperature") || sortBy.equals("density") || sortBy.equals("esi");
    }


}
