package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.PhotometricCurve;
import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet;
import ch.uzh.ifi.hase.soprafs24.entity.DataPoint;
import ch.uzh.ifi.hase.soprafs24.repository.PhotometricCurveRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ExoplanetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.xml.sax.InputSource;
import java.net.URLEncoder;



import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@Service
public class PhotometricCurveService {

    private final PhotometricCurveRepository photometricCurveRepository;
    private final ExoplanetRepository exoplanetRepository;

    private static final String TAP_API_URL = "https://exoplanetarchive.ipac.caltech.edu/TAP/sync";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final float SOLAR_RADIUS_TO_EARTH = 109f;
    private static final float SURFACE_TEMP_EARTH = 288f;

    private static final float ESI_RW = 0.57f;
    private static final float ESI_DW = 1.07f;
    private static final float ESI_VW = 0.70f;
    private static final float ESI_TW = 5.58f;

    public PhotometricCurveService(PhotometricCurveRepository photometricCurveRepository,
                                    ExoplanetRepository exoplanetRepository) {
        this.photometricCurveRepository = photometricCurveRepository;
        this.exoplanetRepository = exoplanetRepository;
    }

    public PhotometricCurve processAndSavePhotometricCurve(MultipartFile file, String hostStar, String planetName, String ownerId) throws IOException {
        List<DataPoint> dataPoints = new ArrayList<>();
        Map<String, String> metadata = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean inMetadata = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("# VarAstro Metadata Begin")) {
                    inMetadata = true;
                    continue;
                }
                if (line.startsWith("# VarAstro Metadata End")) {
                    inMetadata = false;
                    continue;
                }
                if (inMetadata && line.startsWith("#")) {
                    String[] metaParts = line.substring(1).trim().split(":", 2);
                    if (metaParts.length == 2) {
                        metadata.put(metaParts[0].trim(), metaParts[1].trim());
                    }
                    continue;
                }

                DataPoint dataPoint = parseDataPoint(line);
                if (dataPoint != null) {
                    dataPoints.add(dataPoint);
                }
            }
        }

        float fractionalDepth = calculateFractionalDepth(dataPoints);

        Exoplanet exoplanet = calculateExoplanetData(hostStar, planetName, fractionalDepth);
        exoplanet.setOwnerId(ownerId);
        exoplanet = exoplanetRepository.save(exoplanet);
        
        PhotometricCurve curve = new PhotometricCurve();

        curve.setFileName(file.getOriginalFilename());
        curve.setDataPoints(dataPoints);
        curve.setMetadata(metadata);
        curve.setExoplanetId(exoplanet.getId());
        curve.setOwnerId(ownerId);
        photometricCurveRepository.save(curve);
        exoplanet.setPhotometricCurveId(curve.getId());
        exoplanet = exoplanetRepository.save(exoplanet);
        return photometricCurveRepository.save(curve);
    }

    public PhotometricCurve getPhotometricCurveById(String id) {
        return photometricCurveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Photometric curve not found"));
    }

    private DataPoint parseDataPoint(String line) {
        if (line.startsWith("#") || line.trim().isEmpty()) return null;
        if (line.startsWith("JD") || line.startsWith("Aperture")) return null;
        try {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 3) {
                float jd = Float.parseFloat(parts[0]);
                float mag = Float.parseFloat(parts[1]);
                float err = Float.parseFloat(parts[2]);
                return new DataPoint(jd, mag, err);
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }

    public Exoplanet calculateExoplanetData(String hostStar, String planetName, Float fractionalDepth) {
        Map<String, Float> exoplanetData = fetchExoplanetDataFromAPI(planetName);
        
        if (exoplanetData.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "No valid astronomical data was found for that exoplanet in the NASA APIs.");
    }
        Exoplanet exoplanet = new Exoplanet();
        exoplanet.setPlanetName(planetName);
        exoplanet.setHostStarName(hostStar);
        exoplanet.setFractionalDepth(fractionalDepth);
        exoplanet.setOrbitalPeriod(exoplanetData.get("orbitalPeriod"));
        exoplanet.setMass(exoplanetData.get("mass"));
        exoplanet.setTheoreticalTemperature(exoplanetData.get("theoretical_temperature"));
        exoplanet.setRadius(calculateRadius(fractionalDepth, exoplanetData.get("star_radius")));
        exoplanet.setSurfaceGravity(calculateSurfaceGravity(exoplanet.getMass(), exoplanet.getRadius()));
        exoplanet.setEscapeVelocity(calculateEscapeVelocity(exoplanet.getMass(), exoplanet.getRadius()));
        exoplanet.setDensity(calculateDensity(exoplanet.getMass(), exoplanet.getRadius()));
        exoplanet.setEarthSimilarityIndex(calculateESI(exoplanet.getRadius(), exoplanet.getDensity(), exoplanet.getEscapeVelocity(), exoplanet.getTheoreticalTemperature()));
        return exoplanet;
    }

    
    public Map<String, Float> fetchExoplanetDataFromAPI(String planetName) {
        Map<String, Float> data = new HashMap<>();

        try {
            String adqlQuery = String.format("""
                    SELECT pl_name, st_rad, pl_orbper, pl_masse, pl_eqt
                    FROM ps
                    WHERE pl_name = '%s' AND st_rad IS NOT NULL AND pl_orbper IS NOT NULL AND pl_masse IS NOT NULL AND pl_eqt IS NOT NULL
                    """, planetName);

            String requestBody = "query=" + URLEncoder.encode(adqlQuery, StandardCharsets.UTF_8) +
                                 "&format=votable";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TAP_API_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("[NASA TAP] Response status code: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("[NASA TAP] Response body:\n" + response.body());
                data = parseVOTableData(response.body());

                if (data.isEmpty()) {
                    System.out.println("[NASA TAP] Some data is empty.");
                }
            } else {
                System.out.println("[NASA TAP] Failed with HTTP code: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return data;
    }

    
    private Map<String, Float> parseVOTableData(String xmlResponse) {
        Map<String, Float> data = new HashMap<>();
        try {
            System.out.println("[VOT Parse] Starting XML Parsing...");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));
    
            NodeList rows = document.getElementsByTagName("TR");
            System.out.println("[VOT Parse] Number of TR rows: " + rows.getLength());
    
            if (rows.getLength() > 0) {
                NodeList cells = rows.item(0).getChildNodes();
                System.out.println("[VOT Parse] Number of TD cells in first row: " + cells.getLength());
    
                for (int i = 0, j = 0; i < cells.getLength(); i++) {
                    Node cell = cells.item(i);
                    if (cell.getNodeName().equals("TD")) {
                        String value = cell.getTextContent().trim();
                        System.out.println("  [VOT Parse] TD #" + j + " raw value: '" + value + "'");
    
                        try {
                            float parsedValue = Float.parseFloat(value);
                            switch (j) {
                                case 1 -> {
                                    data.put("star_radius", parsedValue);
                                    System.out.println("    -> star_radius = " + parsedValue);
                                }
                                case 2 -> {
                                    data.put("orbitalPeriod", parsedValue);
                                    System.out.println("    -> orbitalPeriod = " + parsedValue);
                                }
                                case 3 -> {
                                    data.put("mass", parsedValue);
                                    System.out.println("    -> mass = " + parsedValue);
                                }
                                case 4 -> {
                                    data.put("theoretical_temperature", parsedValue);
                                    System.out.println("    -> theoretical_temperature = " + parsedValue);
                                }
                                default -> System.out.println("    -> TD #" + j + " not used.");
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("    [WARN] Could not parse value to float: '" + value + "'");
                        }
    
                        j++;
                    }
                }
            } else {
                System.out.println("[VOT Parse] No data rows found in XML.");
            }
        } catch (Exception e) {
            System.out.println("Error parsing XML response: " + e.getMessage());
            e.printStackTrace();
        }
    
        System.out.println("[VOT Parse] Final parsed values: " + data);
        
        if (!data.containsKey("star_radius") || !data.containsKey("orbitalPeriod") ||
        !data.containsKey("mass") || !data.containsKey("theoretical_temperature")) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Not enough data was found for this exoplanet. Please try with another one."
        );
    }
        return data;
    }
    
    
    private float magnitudeToFlux(float magnitude) {
        return (float) Math.pow(10, -0.4 * magnitude);
    }

    private float calculateFractionalDepth(List<DataPoint> dataPoints) {
        List<Float> fluxValues = dataPoints.stream().map(dp -> magnitudeToFlux(dp.getBrightness())).sorted().collect(Collectors.toList());
        int segment = fluxValues.size() / 10;
        float Bmax = (float) fluxValues.subList(fluxValues.size() - segment, fluxValues.size()).stream().mapToDouble(Float::doubleValue).average().orElse(1);
        float Bmin = (float) fluxValues.subList(0, segment).stream().mapToDouble(Float::doubleValue).average().orElse(0);
        return (Bmax - Bmin) / Bmax;
    }

    private float calculateRadius(Float depth, Float starRadius) {
        return starRadius * SOLAR_RADIUS_TO_EARTH * (float) sqrt(depth);
    }

    private float calculateSurfaceGravity(Float mass, Float radius) {
        return mass / (float) pow(radius, 2);
    }

    private float calculateEscapeVelocity(Float mass, Float radius) {
        return (float) sqrt(mass / radius);
    }

    private float calculateDensity(Float mass, Float radius) {
        return mass / (float) pow(radius, 3);
    }

    private float calculateESI(Float r, Float d, Float v, Float t) {
        float esi_r = (float) pow(1 - Math.abs((r - 1) / (r + 1)), ESI_RW / 4);
        float esi_d = (float) pow(1 - Math.abs((d - 1) / (d + 1)), ESI_DW / 4);
        float esi_v = (float) pow(1 - Math.abs((v - 1) / (v + 1)), ESI_VW / 4);
        float esi_t = (float) pow(1 - Math.abs((t - SURFACE_TEMP_EARTH) / (t + SURFACE_TEMP_EARTH)), ESI_TW / 4);
        return esi_r * esi_d * esi_v * esi_t;
    }
}
