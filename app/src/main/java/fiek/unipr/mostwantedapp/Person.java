package fiek.unipr.mostwantedapp;

import android.net.Uri;

public class Person {
    String fullName, address, eyeColor, hairColor, phy_appearance, acts, urlOfProfile, status;
    Integer age, height, weight, longitude, latitude;

    public Person(String fullName, String address, String eyeColor, String hairColor, String phy_appearance, String acts, String urlOfProfile, String status, Integer age, Integer height, Integer weight) {
        this.fullName = fullName;
        this.address = address;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.phy_appearance = phy_appearance;
        this.acts = acts;
        this.urlOfProfile = urlOfProfile;
        this.status = status;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public Person() {

    }

    public Person(String fullName, Integer longitude, Integer latitude)
    {
        this.fullName = fullName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getPhy_appearance() {
        return phy_appearance;
    }

    public void setPhy_appearance(String phy_appearance) {
        this.phy_appearance = phy_appearance;
    }

    public String getActs() {
        return acts;
    }

    public void setActs(String acts) {
        this.acts = acts;
    }

    public String getUrlOfProfile() {
        return urlOfProfile;
    }

    public void setUrlOfProfile(String urlOfProfile) {
        this.urlOfProfile = urlOfProfile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
