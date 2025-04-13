package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.service.PhotometricCurveService;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/photometric-curves")
public class PhotometricCurveController {

    private final PhotometricCurveService photometricCurveService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private ExoplanetRepository exoplanetRepository;


    public PhotometricCurveController(PhotometricCurveService photometricCurveService, SimpMessagingTemplate messagingTemplate, UserService userService, ExoplanetRepository exoplanetRepository) {
        this.photometricCurveService = photometricCurveService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.exoplanetRepository = exoplanetRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotometricCurve> uploadPhotometricCurve(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hostStar") String hostStar,
            @RequestParam("exoplanet") String exoplanet,
            @RequestParam("ownerId") String ownerId) {
        try {
            PhotometricCurve curve = photometricCurveService.processAndSavePhotometricCurve(file, hostStar, exoplanet, ownerId);

            User user = userService.getUserById(ownerId);
            UserGetDTO userDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
            String exoplanetId = curve.getExoplanetId();
            Exoplanet exoplanetEntity = exoplanetRepository.findById(exoplanetId).orElseThrow(() -> 
            new ResponseStatusException(HttpStatus.NOT_FOUND, "new Exoplanet not found"));

            Map<String, Object> notification = new HashMap<>();
            notification.put("user", userDTO);
            notification.put("exoplanet", exoplanetEntity);            

            messagingTemplate.convertAndSend("/topic/exoplanets", notification);
            return ResponseEntity.status(HttpStatus.CREATED).body(curve);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotometricCurve> getPhotometricCurveById(@PathVariable String id) {
        PhotometricCurve curve = photometricCurveService.getPhotometricCurveById(id);
        if (curve != null) {
            return ResponseEntity.ok(curve);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photometric curve not found");
        }
    }
}
