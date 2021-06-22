package fiek.unipr.mostwantedapp;

public class Person {
    String fullName, address, et_eyeColor, et_hairColor, et_phy_appearance, et_acts;
    Integer age, et_height, et_weight;

    public Person(String fullName, String address, String et_eyeColor, String et_hairColor, String et_phy_appearance, String et_acts, Integer age, Integer et_height, Integer et_weight) {
        this.fullName = fullName;
        this.address = address;
        this.et_eyeColor = et_eyeColor;
        this.et_hairColor = et_hairColor;
        this.et_phy_appearance = et_phy_appearance;
        this.et_acts = et_acts;
        this.age = age;
        this.et_height = et_height;
        this.et_weight = et_weight;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getEt_eyeColor() {
        return et_eyeColor;
    }

    public String getEt_hairColor() {
        return et_hairColor;
    }

    public String getEt_phy_appearance() {
        return et_phy_appearance;
    }

    public String getEt_acts() {
        return et_acts;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getEt_height() {
        return et_height;
    }

    public Integer getEt_weight() {
        return et_weight;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEt_eyeColor(String et_eyeColor) {
        this.et_eyeColor = et_eyeColor;
    }

    public void setEt_hairColor(String et_hairColor) {
        this.et_hairColor = et_hairColor;
    }

    public void setEt_phy_appearance(String et_phy_appearance) {
        this.et_phy_appearance = et_phy_appearance;
    }

    public void setEt_acts(String et_acts) {
        this.et_acts = et_acts;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setEt_height(Integer et_height) {
        this.et_height = et_height;
    }

    public void setEt_weight(Integer et_weight) {
        this.et_weight = et_weight;
    }

}
