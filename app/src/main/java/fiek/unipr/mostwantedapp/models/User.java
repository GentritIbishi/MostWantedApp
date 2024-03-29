package fiek.unipr.mostwantedapp.models;

public class User {

    private String userID, name, lastname,
            fullName, address, email,
            parentName, gender,
            role, phone, personal_number,
            register_date_time, grade,
            password, urlOfProfile;

    private Double balance, totalPaid;
    private Boolean isEmailVerified = false;

    public User() {
    }

    public User(String userID, String name, String lastname, String fullName, String address, String email, String parentName, String gender, String role, String phone, String personal_number, String register_date_time, String grade, String password, String urlOfProfile, Double balance, Double totalPaid, Boolean isEmailVerified) {
        this.userID = userID;
        this.name = name;
        this.lastname = lastname;
        this.fullName = fullName;
        this.address = address;
        this.email = email;
        this.parentName = parentName;
        this.gender = gender;
        this.role = role;
        this.phone = phone;
        this.personal_number = personal_number;
        this.register_date_time = register_date_time;
        this.grade = grade;
        this.password = password;
        this.urlOfProfile = urlOfProfile;
        this.balance = balance;
        this.totalPaid = totalPaid;
        this.isEmailVerified = isEmailVerified;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}
