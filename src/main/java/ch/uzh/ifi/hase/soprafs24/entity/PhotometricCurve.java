package ch.uzh.ifi.hase.soprafs24.entity;
//import exoplanet

import javax.persistence.*;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PhotometricCurve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @ManyToOne
    @JoinColumn(name = "planet_id", nullable = false)
    private Exoplanet planet;

    @OneToMany(mappedBy = "curve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataPoint> dataPoints = new ArrayList<>();

    //void constructor, required by JPA
    public PhotometricCurve() {
    }

    public PhotometricCurve(Long id, String fileName, Exoplanet planet, List<DataPoint> dataPoints) {
        this.id = id;
        this.fileName = fileName;
        this.planet = planet;
        this.dataPoints = dataPoints;
    }

    //getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

     public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Exoplanet getPlanet() {
        return planet;
    }

    public void setPlanet(Exoplanet planet) {
        this.planet = planet;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}