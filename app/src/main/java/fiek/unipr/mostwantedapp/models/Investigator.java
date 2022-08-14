package fiek.unipr.mostwantedapp.models;

public class Investigator {
    private String investigator_id, firstName, lastName, parentName, fullName, address, eyeColor, hairColor, phy_appearance, urlOfProfile, registration_date, age, gender, height, weight;

    public Investigator(String investigator_id, String firstName, String lastName, String parentName, String fullName, String address, String eyeColor, String hairColor, String phy_appearance, String urlOfProfile, String registration_date, String age, String gender, String height, String weight) {
        this.investigator_id = investigator_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentName = parentName;
        this.fullName = fullName;
        this.address = address;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.phy_appearance = phy_appearance;
        this.urlOfProfile = urlOfProfile;
        this.registration_date = registration_date;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
    }

    public String getInvestigator_id() {
        return investigator_id;
    }

    public void setInvestigator_id(String investigator_id) {
        this.investigator_id = investigator_id;
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

    public String getUrlOfProfile() {
        return urlOfProfile;
    }

    public void setUrlOfProfile(String urlOfProfile) {
        this.urlOfProfile = urlOfProfile;
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
}
