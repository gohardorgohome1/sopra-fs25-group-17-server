package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "photometric_curves")
public class PhotometricCurve implements Serializable {

    @Id
    private String id;

    private String fileName;
    private String exoplanetId;
    private String ownerId;

    private List<DataPoint> dataPoints = new ArrayList<>();
    private Map<String, String> metadata = new HashMap<>();

    public PhotometricCurve() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExoplanetId() {
        return exoplanetId;
    }

    public void setExoplanetId(String exoplanetId) {
        this.exoplanetId = exoplanetId;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}