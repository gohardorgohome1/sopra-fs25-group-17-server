package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ExoplanetGetDTO {
    private String id;
    private String planetName;
    private String hostStarName;
    private String photometricCurveId; // Reference to the PhotometricCurve
    private float fractionalDepth;
    private float density;
    private float orbitalPeriod;
    private float radius;
    private float surfaceGravity;
    private float theoreticalTemperature;
    private float mass;
    private float escapeVelocity;
    private float earthSimilarityIndex;

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
    public float getFractionalDepth() {
        return fractionalDepth;
    }

    public void setFractionalDepth(float fractionalDepth) {
        this.fractionalDepth = fractionalDepth;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public void setOrbitalPeriod(float orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getSurfaceGravity() {
        return surfaceGravity;
    }

    public void setSurfaceGravity(float surfaceGravity) {
        this.surfaceGravity = surfaceGravity;
    }

    public float getTheoreticalTemperature() {
        return theoreticalTemperature;
    }

    public void setTheoreticalTemperature(float theoreticalTemperature) {
        this.theoreticalTemperature = theoreticalTemperature;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getEscapeVelocity() {
        return escapeVelocity;
    }

    public void setEscapeVelocity(float escapeVelocity) {
        this.escapeVelocity = escapeVelocity;
    }

    public float getEarthSimilarityIndex() {
        return earthSimilarityIndex;
    }

    public void setEarthSimilarityIndex(float earthSimilarityIndex) {
        this.earthSimilarityIndex = earthSimilarityIndex;
    }

}
