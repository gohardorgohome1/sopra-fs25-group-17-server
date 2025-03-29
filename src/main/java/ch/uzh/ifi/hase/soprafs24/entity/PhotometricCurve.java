package ch.uzh.ifi.hase.soprafs24.entity;
//import exoplanet

import javax.persistence.*;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

@Document(collection = "photometric_curves") //MongoDB collection
public class PhotometricCurve implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String filename;

    @DBRef  // Reference to another collection (optional)
    private Exoplanet planet;

    private List<DataPoint> dataPoints = new ArrayList<>(); // Embedded list of DataPoints
    private Map<String, String> metadata = new HashMap<>();

    //void constructor, required by JPA
    public PhotometricCurve() {
    }

    public PhotometricCurve(Long id, String fileName, Exoplanet planet, List<DataPoint> dataPoints) {
        this.fileName = fileName;
        this.planet = planet;
        this.dataPoints = dataPoints;
    }

    //getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
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