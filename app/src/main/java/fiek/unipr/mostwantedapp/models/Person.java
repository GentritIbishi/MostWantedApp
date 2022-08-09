package fiek.unipr.mostwantedapp.models;

public class Person {
    private String firstName, lastName, parentName, fullName, address, eyeColor, hairColor, phy_appearance, acts, urlOfProfile, status, prize, registration_date;
    private Integer age, height, weight;
    private Double longitude, latitude;

    public Person() {}

    public Person(String firstName, String lastName, String parentName, String fullName, String address, String eyeColor, String hairColor, String phy_appearance, String acts, String urlOfProfile, String status, String prize, String registration_date, Integer age, Integer height, Integer weight, Double longitude, Double latitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentName = parentName;
        this.fullName = fullName;
        this.address = address;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.phy_appearance = phy_appearance;
        this.acts = acts;
        this.urlOfProfile = urlOfProfile;
        this.status = status;
        this.prize = prize;
        this.registration_date = registration_date;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.longitude = longitude;
        this.latitude = latitude;
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
