package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class DataPointGetDTO {

    private Long id;
    private Float time; 
    private Float brightness;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }  

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public Float getBrightness() {
        return brightness;
    }

    public void setBrightness(Float brightness) {
        this.brightness = brightness;
    }
}

