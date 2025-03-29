package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.service.PhotometricCurveService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PhotometricCurveGetDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/photometric-curves")
public class PhotometricCurveController {

    private final PhotometricCurveService photometricCurveService;

    public PhotometricCurveController(PhotometricCurveService photometricCurveService) {
        this.photometricCurveService = photometricCurveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhotometricCurve(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hostStar") String hostStar,
            @RequestParam("exoplanet") String exoplanet) {
        try {
            // Validate exoplanet and host star using NASA TAP API (To be implemented)
            // We need to use the API here to find if the exoplanet exists, so we can retrieve the data
            
            if (!photometricCurveService.validateExoplanet(hostStar, exoplanet)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Exoplanet or host star not found in NASA TAP API.");
            }
    
            PhotometricCurve curve = photometricCurveService.processAndSavePhotometricCurve(file, hostStar, exoplanet);
            return ResponseEntity.status(HttpStatus.CREATED).body(curve);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPhotometricCurveById(@PathVariable Long id) {
        PhotometricCurve curve = photometricCurveService.getPhotometricCurveById(id);
        if (curve != null) {
            return ResponseEntity.ok(curve);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photometric curve not found");
        }
    }

}
