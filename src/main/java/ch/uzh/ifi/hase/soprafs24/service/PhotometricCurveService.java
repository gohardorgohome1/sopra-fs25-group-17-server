package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.DataPoint;
import ch.uzh.ifi.hase.soprafs24.repository.PhotometricCurveRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import static java.lang.Math.sqrt;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;

@Service
public class PhotometricCurveService {

    private final PhotometricCurveRepository photometricCurveRepository;
    private final ExoplanetRepository exoplanetRepository;
    // NASA TAP Service URL
    private static final String TAP_API_URL = "https://exoplanetarchive.ipac.caltech.edu/TAP";

    // ADQL Query
    private static final String QUERY = "SELECT pl_name, st_rad, pl_orbper, pl_masse, pl_eqt " +
                                        "FROM ps " +
                                        "WHERE pl_name = '%s' AND st_rad IS NOT NULL AND pl_orbper IS NOT NULL AND pl_masse IS NOT NULL AND pl_eqt IS NOT NULL";
    // HttpClient for sending requests
    private static final HttpClient client = HttpClient.newHttpClient();
    
    // Astronomical constants
    private static final float SOLAR_RADIUS_TO_EARTH = 109f; // 1 R☉ = 109 R⊕
    private static final float surface_temp_earth = 288f;  // Kelvin

    // ESI Calculation Weights (Schulze-Makuch 2011)
    private static final float ESI_RW = 0.57f;
    private static final float ESI_DW = 1.07f;
    private static final float ESI_VW = 0.70f;
    private static final float ESI_TW = 5.58f;

    public PhotometricCurveService(PhotometricCurveRepository photometricCurveRepository,
                                    ExoplanetRepository exoplanetRepository) {
        this.photometricCurveRepository = photometricCurveRepository;
        this.exoplanetRepository = exoplanetRepository;
    }

    public PhotometricCurve processAndSavePhotometricCurve(MultipartFile file, String hostStar, String planetName) throws IOException {
        List<DataPoint> dataPoints = new ArrayList<>();
        Map<String, String> metadata = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean inMetadata = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Parse metadata section
                if (line.startsWith("# VarAstro Metadata Begin")) {
                    inMetadata = true;
                    continue;
                }
                if (line.startsWith("# VarAstro Metadata End")) {
                    inMetadata = false;
                    continue;
                }
                if (inMetadata && line.startsWith("#")) {
                    // Parse metadata lines like: # StarName: TrES-3
                    String[] metaParts = line.substring(1).trim().split(":", 2);
                    if (metaParts.length == 2) {
                        metadata.put(metaParts[0].trim(), metaParts[1].trim());
                    }
                    continue;
                }
                
                // Parse data points
                DataPoint dataPoint = parseDataPoint(line);
                if (dataPoint != null) {
                    dataPoints.add(dataPoint);
                }
            }
        }

        // Create and save the photometric curve
        PhotometricCurve curve = new PhotometricCurve();
        curve.setFileName(file.getOriginalFilename());
        curve.setDataPoints(dataPoints);
        // Calculate fractional depth from data points
        float fractionalDepth = calculateFractionalDepth(dataPoints);

        Exoplanet exoplanet = calculateExoplanetData(hostStar, planetName, fractionalDepth);
        exoplanet.setPhotometricCurve(curve);
        exoplanet = exoplanetRepository.save(exoplanet);


        curve.setPlanet(exoplanet);

        // Save to MongoDB
        return photometricCurveRepository.save(curve);
    }

    public PhotometricCurve getPhotometricCurveById(Long id) {
        Optional<PhotometricCurve> curve = photometricCurveRepository.findById(id);
        return curve.orElse(null); // Returns the curve if found, otherwise null
    }

    private DataPoint parseDataPoint(String line) {
        // Skip metadata lines and empty lines
        if (line.startsWith("#") || line.trim().isEmpty()) {
            return null;
        }
        
        // Skip header line (JD V-C s1)
        if (line.startsWith("JD") || line.startsWith("Aperture")) {
            return null;
        }
        
        try {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                float jd = Float.parseFloat(parts[0]);
                float magnitude = Float.parseFloat(parts[1]);
                float error = Float.parseFloat(parts[2]);
                return new DataPoint(jd, magnitude, error);
            }
        } catch (NumberFormatException e) {
            // Handle parse errors if needed
        }
        return null;
    }

    
    /**
     * Dummy method to fetch radius, orbital period, and mass from NASA TAP API.
     * The rest of the values will be calculated from these.
     */
    public Exoplanet calculateExoplanetData(String hostStar, String planetName, Float fractionalDepth) {

        Map<String, Float> exoplanetData = fetchExoplanetDataFromAPI(hostStar, planetName);

        Exoplanet exoplanet = new Exoplanet();
        exoplanet.setPlanetName(planetName);
        exoplanet.setHostStarName(hostStar);
        exoplanet.setFractionalDepth(fractionalDepth);
        exoplanet.setOrbitalPeriod(exoplanetData.get("orbitalPeriod"));
        exoplanet.setMass(exoplanetData.get("mass")); 
        exoplanet.setTheoreticalTemperature(exoplanetData.get("theoretical_temperature")); 

        // Calculate other properties based on these values
        exoplanet.setRadius(calculateRadius(exoplanet.getFractionalDepth(), exoplanetData.get("star_radius")));
        exoplanet.setSurfaceGravity(calculateSurfaceGravity(exoplanetData.get("mass"), exoplanet.getRadius()));
        exoplanet.setEscapeVelocity(calculateEscapeVelocity(exoplanetData.get("mass"), exoplanet.getRadius()));
        exoplanet.setDensity(calculateDensity(exoplanetData.get("mass"), exoplanet.getRadius()));
        exoplanet.setEarthSimilarityIndex(calculateESI(exoplanet.getRadius(), exoplanet.getDensity(), exoplanet.getEscapeVelocity(), exoplanet.getTheoreticalTemperature()));

        return exoplanet;
    }

    public Map<String, Float> fetchExoplanetDataFromAPI(String hostStar, String planetName) {
        Map<String, Float> data = new HashMap<>();

        try {
            // Create the URL for the API request with the formatted planet name
            String queryUrl = String.format(TAP_API_URL + "?query=" + QUERY, planetName);
            URI uri = URI.create(queryUrl);

            // Create the GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response was successful
            if (response.statusCode() == 200) {
                // Parse the VOTable XML response
                String xmlResponse = response.body();
                data = parseVOTableData(xmlResponse);
            } else {
                System.out.println("Error: Unable to fetch data (HTTP status " + response.statusCode() + ")");
            }

        } catch (Exception e) {
            System.out.println("Error fetching data from TAP API: " + e.getMessage());
        }

        return data;
    }

    private Map<String, Float> parseVOTableData(String xmlResponse) {
        Map<String, Float> data = new HashMap<>();

        try {
            // Initialize XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

            // Extract the relevant data from the VOTable XML
            NodeList rows = document.getElementsByTagName("TR");
            if (rows.getLength() > 0) {
                Node row = rows.item(0); // Taking the first row (most recent)
                NodeList cells = row.getChildNodes();

                for (int i = 0; i < cells.getLength(); i++) {
                    Node cell = cells.item(i);
                    if (cell.getNodeName().equals("TD")) {
                        String value = cell.getTextContent();
                        switch (i) {
                            case 1:
                                data.put("star_radius", Float.parseFloat(value));  // star radius in solar radii
                                break;
                            case 2:
                                data.put("orbitalPeriod", Float.parseFloat(value));  // orbital period in days
                                break;
                            case 3:
                                data.put("mass", Float.parseFloat(value));  // mass in Earth masses
                                break;
                            case 4:
                                data.put("theoretical_temperature", Float.parseFloat(value));  // temperature in Kelvin
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing XML response: " + e.getMessage());
        }

        return data;
    }

    //convert magnitude to flux
    private float magnitudeToFlux(float magnitude) {
        return (float) Math.pow(10, -0.4 * magnitude);
    }

    // Dummy method for calculating the fractional depth with the data points
    // Still have to check if it should work like this
    private float calculateFractionalDepth(List<DataPoint> dataPoints) {
        List<Float> brightnessValues = dataPoints.stream()
            .map(dp -> magnitudeToFlux(dp.getBrightness()))
            .sorted()
            .toList();

        int segmentSize = brightnessValues.size() / 10; // Top & bottom 10%

        float Bmax = brightnessValues.subList(brightnessValues.size() - segmentSize, brightnessValues.size())
            .stream().mapToDouble(Float::doubleValue).average().orElseThrow();
        float Bmin = brightnessValues.subList(0, segmentSize)
            .stream().mapToDouble(Float::doubleValue).average().orElseThrow();

        return (Bmax - Bmin) / Bmax;
    }

    private float calculateRadius(Float fractionalDepth, Float star_radius) {
        // in earth radius units!!
        float radius = (float) (star_radius * SOLAR_RADIUS_TO_EARTH * sqrt(fractionalDepth)); 
        return radius;
    }

    private float calculateSurfaceGravity(Float mass, Float radius) {
        // in earth surface gravity units!!
        float surfaceGravity = (float) (mass / Math.pow(radius, 2));
        return surfaceGravity;
    }

    private float calculateEscapeVelocity(Float mass, Float radius) {
        // in earth escape velocity units!! 
        float escapeVelocity = (float) sqrt(mass / radius);
        return escapeVelocity;
    }

    private float calculateDensity(Float mass, Float radius) {
        // in earth density units!!
        float density = (float) (mass / Math.pow(radius, 3));
        return density;
    }

    private float calculateESI(Float radius, Float density, Float velocity, Float temperature) {
        // This is an approximate formula where each characteristic has its own weight
        // All the exoplanet measurements are in Earth units (Earth always = 1)
        // Exoplanet example: radius = 2.5 Earth radii, density = 1.2 Earth densities, etc.

        float ESI_r = (float) Math.pow((1 - Math.abs( (radius - 1) / (radius + 1) )), ESI_RW/4);
        float ESI_d = (float) Math.pow((1 - Math.abs( (density - 1)/(density + 1) )), ESI_DW/4);
        float ESI_v = (float) Math.pow((1 - Math.abs( (velocity - 1)/(velocity + 1) )), ESI_VW/4);
        float ESI_t = (float) Math.pow((1 - Math.abs( (temperature - surface_temp_earth)/(temperature + surface_temp_earth) )), ESI_TW/4);

        float ESI = (float) (ESI_r * ESI_d * ESI_v * ESI_t);
        return ESI;
    }
}
