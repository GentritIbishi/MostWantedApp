package fiek.unipr.mostwantedapp.models;

import java.util.HashMap;
import java.util.Map;

public class Report {
    private String Description, Date_time, uID, informer_fullName;
    private ReportStatus status = ReportStatus.UNVERIFIED;
    private Double longitude, latitude;
    private Map<String, Object> images = new HashMap<>();

    public Report() {
    }

    public Report(String description, String date_time, String uID, String informer_fullName, ReportStatus status, Double longitude, Double latitude, Map<String, Object> images) {
        Description = description;
        Date_time = date_time;
        this.uID = uID;
        this.informer_fullName = informer_fullName;
        this.status = status;
        this.longitude = longitude;
        this.latitude = latitude;
        this.images = images;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate_time() {
        return Date_time;
    }

    public void setDate_time(String date_time) {
        Date_time = date_time;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getInformer_fullName() {
        return informer_fullName;
    }

    public void setInformer_fullName(String informer_fullName) {
        this.informer_fullName = informer_fullName;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Map<String, Object> getImages() {
        return images;
    }

    public void setImages(Map<String, Object> images) {
        this.images = images;
    }
}
