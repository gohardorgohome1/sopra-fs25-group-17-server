package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "EXOPLANET")
public class Exoplanet implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String planetName;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne(mappedBy = "planet", fetch = FetchType.LAZY)
    private PhotometricCurve photometricCurve;

    @ElementCollection
    @CollectionTable(name = "EXOPLANET_COMMENTS", joinColumns = @JoinColumn(name = "exoplanet_id"))
    @Column(name = "comment")
    private List<String> comments;

    @Column(nullable = false)
    private String hostStarName;

    @Column(nullable = false)
    private float fractionalDepth;

    @Column(nullable = false)
    private float density;

    @Column(nullable = false)
    private float orbitalPeriod;

    @Column(nullable = false)
    private float radius;

    @Column(nullable = false)
    private float surfaceGravity;

    @Column(nullable = false)
    private float theoreticalTemperature;

    @Column(nullable = false)
    private float mass;

    @Column(nullable = false)
    private float escapeVelocity;

    @Column(nullable = false)
    private float earthSimilarityIndex;



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanetName() {
        return planetName;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public PhotometricCurve getPhotometricCurve() {
        return photometricCurve;
    }

    public void setPhotometricCurve(PhotometricCurve photometricCurve) {
        this.photometricCurve = photometricCurve;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
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


}
