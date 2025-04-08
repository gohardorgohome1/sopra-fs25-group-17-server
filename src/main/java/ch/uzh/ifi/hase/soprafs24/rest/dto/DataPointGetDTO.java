package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class DataPointGetDTO {

    private Double time;
    private Float brightness;
    private Float brightnessError;

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Float getBrightness() {
        return brightness;
    }

    public void setBrightness(Float brightness) {
        this.brightness = brightness;
    }

    public Float getBrightnessError() {
        return brightnessError;
    }

    public void setBrightnessError(Float brightnessError) {
        this.brightnessError = brightnessError;
    }
}