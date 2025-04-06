package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.service.PhotometricCurveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/photometric-curves")
public class PhotometricCurveController {

    private final PhotometricCurveService photometricCurveService;

    public PhotometricCurveController(PhotometricCurveService photometricCurveService) {
        this.photometricCurveService = photometricCurveService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotometricCurve> uploadPhotometricCurve(
            @RequestParam("file") MultipartFile file,
            @RequestParam("hostStar") String hostStar,
            @RequestParam("exoplanet") String exoplanet,
            @RequestParam("ownerId") String ownerId) {
        try {
            PhotometricCurve curve = photometricCurveService.processAndSavePhotometricCurve(file, hostStar, exoplanet, ownerId);
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
