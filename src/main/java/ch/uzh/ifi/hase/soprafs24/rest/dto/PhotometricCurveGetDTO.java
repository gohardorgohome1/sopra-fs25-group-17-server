package ch.uzh.ifi.hase.soprafs24.rest.dto;
import ch.uzh.ifi.hase.soprafs24.entity.Exoplanet; 
import ch.uzh.ifi.hase.soprafs24.rest.dto.DataPointGetDTO;  


public class PhotometricCurveGetDTO {
    private Long id;
    private String fileName;
    private Exoplanet planet;
    private List<DataPointGetDTO> dataPoints;

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

    public List<DataPointGetDTO> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPointGetDTO> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
