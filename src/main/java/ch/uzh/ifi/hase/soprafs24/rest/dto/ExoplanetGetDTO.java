package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ExoplanetGetDTO {
    private String id;
    private String planetName;
    private String hostStarName;
    private String photometricCurveId; // Reference to the PhotometricCurve

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanetName() {
        return planetName;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public String getHostStarName() {
        return hostStarName;
    }

    public void setHostStarName(String hostStarName) {
        this.hostStarName = hostStarName;
    }

    public String getPhotometricCurveId() {
        return photometricCurveId;
    }

    public void setPhotometricCurveId(String photometricCurveId) {
        this.photometricCurveId = photometricCurveId;
    }
}
