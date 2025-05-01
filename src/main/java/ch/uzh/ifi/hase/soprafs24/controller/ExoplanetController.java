package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.CommentPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ExoplanetGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.ExoplanetService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Exoplanet Controller
 * This class is responsible for handling all REST requests related to
 * exoplanets.
 * The controller will receive the request and delegate the execution to the
 * ExoplanetService and finally return the result.
 */
@RestController
@RequestMapping("/exoplanets")
public class ExoplanetController {

    private final ExoplanetService exoplanetService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private ExoplanetRepository exoplanetRepository;

    public ExoplanetController(ExoplanetService exoplanetService, SimpMessagingTemplate messagingTemplate, UserService userService, ExoplanetRepository exoplanetRepository) {
        this.messagingTemplate = messagingTemplate;
        this.exoplanetService = exoplanetService;
        this.userService = userService;
        this.exoplanetRepository = exoplanetRepository;
    }

    /**
     * Get all Exoplanets
     * @return List of Exoplanet DTOs
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExoplanetGetDTO> getAllExoplanets() {
        return exoplanetService.getExoplanets().stream()
                .map(DTOMapper.INSTANCE::convertEntityToExoplanetGetDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get Exoplanet by its ID
     * @param id Exoplanet ID
     * @return Exoplanet details in DTO format
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExoplanetGetDTO> getExoplanetById(@PathVariable String id) {
        Exoplanet exoplanet = exoplanetService.getExoplanetById(id);
        return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToExoplanetGetDTO(exoplanet));
    }

    /**
     * Delete an Exoplanet by ID
     * @param id Exoplanet ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExoplanet(@PathVariable String id) {
        exoplanetService.deleteExoplanet(id);
    }

    /**
     * Get ranked Exoplanets based on sorting criteria
     * @param sortBy Criteria: mass, radius, temperature, density, esi
     * @param order asc or desc
     * @return Ranked list of Exoplanets
     */
    @GetMapping("/ranking")
    @ResponseStatus(HttpStatus.OK)
    public List<ExoplanetGetDTO> getExoplanetRanking(
        @RequestParam(value = "sortBy", defaultValue = "esi") String sortBy,
        @RequestParam(value = "order", defaultValue = "desc") String order) {

        if (!isValidSortBy(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sortBy parameter. Allowed values: esi, mass, radius, temperature, density");
        }
        if (!order.equals("asc") && !order.equals("desc")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order parameter. Allowed values: asc, desc.");
        }

        return exoplanetService.getExoplanetRanking(sortBy, order).stream()
                .map(DTOMapper.INSTANCE::convertEntityToExoplanetGetDTO)
                .collect(Collectors.toList());
    }

    private boolean isValidSortBy(String sortBy) {
        return sortBy.equals("mass") || sortBy.equals("radius") || sortBy.equals("temperature") ||
               sortBy.equals("density") || sortBy.equals("esi");
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCommentToExoplanet(@PathVariable String id, @RequestBody CommentPostDTO commentPostDTO) {
        exoplanetService.addComment(id, commentPostDTO);

        // include user + exoplanet info in the message
        User user = userService.getUserById(commentPostDTO.getUserId());
        UserGetDTO userDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        Exoplanet exoplanet = exoplanetRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Exoplanet not found")
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("user", userDTO);
        payload.put("exoplanet", exoplanet);

        // Notify frontend that a new comment was added to this exoplanet
        // This will send a message to subscribers of /topic/comments/{exoplanetId}.
        messagingTemplate.convertAndSend("/topic/comments/" + id, payload);

        // Global comment update
        messagingTemplate.convertAndSend("/topic/comments", payload);
    }

    @GetMapping("/{id}/comments")
    public List<CommentGetDTO> getCommentsForExoplanet(@PathVariable String id) {
        return exoplanetService.getComments(id);
    }
}
