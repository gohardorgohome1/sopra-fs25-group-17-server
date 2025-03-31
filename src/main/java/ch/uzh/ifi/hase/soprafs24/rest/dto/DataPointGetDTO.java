package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class DataPointGetDTO {

    private Float time;
    private Float brightness;

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
