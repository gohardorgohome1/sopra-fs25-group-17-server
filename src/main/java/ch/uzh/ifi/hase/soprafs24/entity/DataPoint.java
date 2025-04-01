package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

public class DataPoint implements Serializable {

    private Float time;
    private Float brightness;
    private Float brightnessError;

    public DataPoint() {}

    public DataPoint(Float time, Float brightness, Float brightnessError) {
        this.time = time;
        this.brightness = brightness;
        this.brightnessError = brightnessError;
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

    public Float getBrightnessError() {
        return brightnessError;
    }

    public void setBrightnessError(Float brightnessError) {
        this.brightnessError = brightnessError;
    }
}
