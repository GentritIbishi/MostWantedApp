package fiek.unipr.mostwantedapp.models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report {
    private String docId, Title, Description, Date_time, uID, personId, informer_person, wanted_person, address, informer_person_urlOfProfile, prizeToWin;
    private ReportStatus status = ReportStatus.UNVERIFIED;
    private Double longitude, latitude;
    private Map<String, Object> images = new HashMap<>();

    public Report() {
    }

    public Report(String docId, String title, String description, String date_time, String uID, String personId, String informer_person, String wanted_person, String address, String informer_person_urlOfProfile, String prizeToWin, ReportStatus status, Double longitude, Double latitude, Map<String, Object> images) {
        this.docId = docId;
        Title = title;
        Description = description;
        Date_time = date_time;
        this.uID = uID;
        this.personId = personId;
        this.informer_person = informer_person;
        this.wanted_person = wanted_person;
        this.address = address;
        this.informer_person_urlOfProfile = informer_person_urlOfProfile;
        this.prizeToWin = prizeToWin;
        this.status = status;
        this.longitude = longitude;
        this.latitude = latitude;
        this.images = images;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getInformer_person() {
        return informer_person;
    }

    public void setInformer_person(String informer_person) {
        this.informer_person = informer_person;
    }

    public String getWanted_person() {
        return wanted_person;
    }

    public void setWanted_person(String wanted_person) {
        this.wanted_person = wanted_person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInformer_person_urlOfProfile() {
        return informer_person_urlOfProfile;
    }

    public void setInformer_person_urlOfProfile(String informer_person_urlOfProfile) {
        this.informer_person_urlOfProfile = informer_person_urlOfProfile;
    }

    public String getPrizeToWin() {
        return prizeToWin;
    }

    public void setPrizeToWin(String prizeToWin) {
        this.prizeToWin = prizeToWin;
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