package fiek.unipr.mostwantedapp.models;

public class User {

    Integer balance;
    String userID, name, lastname, fullName, address, email, parentName, role, phone, personal_number, register_date_time, grade, password, urlOfProfile;
    Boolean isEmailVerified = false;

    public User() {
    }

    public User(String userID, String email, String role, String password) {
        this.userID = userID;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public User(Integer balance, String userID, String name, String lastname, String fullName, String address, String email, String parentName, String role, String phone, String personal_number, String register_date_time, String grade, String password, String urlOfProfile, Boolean isEmailVerified) {
        this.balance = balance;
        this.userID = userID;
        this.name = name;
        this.lastname = lastname;
        this.fullName = fullName;
        this.address = address;
        this.email = email;
        this.parentName = parentName;
        this.role = role;
        this.phone = phone;
        this.personal_number = personal_number;
        this.register_date_time = register_date_time;
        this.grade = grade;
        this.password = password;
        this.urlOfProfile = urlOfProfile;
        this.isEmailVerified = isEmailVerified;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPersonal_number() {
        return personal_number;
    }

    public void setPersonal_number(String personal_number) {
        this.personal_number = personal_number;
    }

    public String getRegister_date_time() {
        return register_date_time;
    }

    public void setRegister_date_time(String register_date_time) {
        this.register_date_time = register_date_time;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrlOfProfile() {
        return urlOfProfile;
    }

    public void setUrlOfProfile(String urlOfProfile) {
        this.urlOfProfile = urlOfProfile;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
