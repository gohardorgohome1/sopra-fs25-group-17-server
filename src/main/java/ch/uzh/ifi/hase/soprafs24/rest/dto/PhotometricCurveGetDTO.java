package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;
import java.util.Map;

public class PhotometricCurveGetDTO {

    private String id;
    private String fileName;
    private String exoplanetId;
    private List<DataPointGetDTO> dataPoints;
    private Map<String, String> metadata;
    private String ownerId;

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

    public List<DataPointGetDTO> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPointGetDTO> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
