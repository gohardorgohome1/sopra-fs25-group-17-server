package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ExoplanetPostDTO {
    private String planetName;
    private String hostStarName;
    private Long photometricCurveId; // Reference to the PhotometricCurve

    // Getters and Setters
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

    public Long getPhotometricCurveId() {
        return photometricCurveId;
    }

    public void setPhotometricCurveId(Long photometricCurveId) {
        this.photometricCurveId = photometricCurveId;
    }
}
