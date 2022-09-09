package fiek.unipr.mostwantedapp.models;

import java.util.List;

public class Person {
    private String personId, firstName, lastName, parentName, fullName, birthday, address, eyeColor, hairColor, phy_appearance, urlOfProfile, status, prize, registration_date, age, gender, height, weight;
    private List<String> acts;
    private Double longitude, latitude;

    public Person() {}

    public Person(String personId, String firstName, String lastName, String parentName, String fullName, String birthday, String address, String eyeColor, String hairColor, String phy_appearance, String urlOfProfile, String status, String prize, String registration_date, String age, String gender, String height, String weight, List<String> acts, Double longitude, Double latitude) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentName = parentName;
        this.fullName = fullName;
        this.birthday = birthday;
        this.address = address;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.phy_appearance = phy_appearance;
        this.urlOfProfile = urlOfProfile;
        this.status = status;
        this.prize = prize;
        this.registration_date = registration_date;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.acts = acts;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public List<String> getActs() {
        return acts;
    }

    public void setActs(List<String> acts) {
        this.acts = acts;
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
}
