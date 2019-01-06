package com.saasbass.basstrackerservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LakeProfile {
    private Long id;
    private String name;
    private String state;
    private Double latitude;
    private Double longitude;

    public LakeProfile(Long id, String name, String state, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "\n" + "--Lake Profile--" + "\n"
                + "Id: " + this.id + "\n"
                + "Name: " + this.name + "\n"
                + "State: " + this.state + "\n"
                + "Latitude: " + this.latitude + "\n"
                + "Longitude: " + this.longitude + "\n";
    }
}
