package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "exoplanets")
public class Exoplanet implements Serializable {

    @Id
    private String id;

    private String planetName;
    private String ownerId;  // Reference to User

    private String hostStarName;
    private float fractionalDepth;
    private float density;
    private float orbitalPeriod;
    private float radius;
    private float surfaceGravity;
    private float theoreticalTemperature;
    private float mass;
    private float escapeVelocity;
    private float earthSimilarityIndex;

    private List<Comment> comments = new ArrayList<>();

    private String photometricCurveId;

    public class Comment {
        private String userId;
        private String message;
        private LocalDateTime createdAt;

        public String getUserId() {
            return userId;
        }
    
        public void setUserId(String userId) {
            this.userId = userId;
        }
    
        public String getMessage() {
            return message;
        }
    
        public void setMessage(String message) {
            this.message = message;
        }
    
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getHostStarName() {
        return hostStarName;
    }

    public void setHostStarName(String hostStarName) {
        this.hostStarName = hostStarName;
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
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPhotometricCurveId() {
        return photometricCurveId;
    }

    public void setPhotometricCurveId(String photometricCurveId) {
        this.photometricCurveId = photometricCurveId;
    }
}
