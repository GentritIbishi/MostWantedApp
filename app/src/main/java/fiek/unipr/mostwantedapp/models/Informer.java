package fiek.unipr.mostwantedapp.models;

import android.net.Uri;

public class Informer {
    Integer balance;
    String userID, name, lastname, fullName, Address, Email, parentName, grade, googleID, phone, personal_number, role;
    Uri photoURL;

    public Informer() {
    }

    public Informer(Integer balance, String userID, String name, String lastname, String fullName, String address, String email, String parentName, String grade, String googleID, String phone, String personal_number, String role, Uri photoURL) {
        this.balance = balance;
        this.userID = userID;
        this.name = name;
        this.lastname = lastname;
        this.fullName = fullName;
        Address = address;
        Email = email;
        this.parentName = parentName;
        this.grade = grade;
        this.googleID = googleID;
        this.phone = phone;
        this.personal_number = personal_number;
        this.role = role;
        this.photoURL = photoURL;
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
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Uri getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(Uri photoURL) {
        this.photoURL = photoURL;
    }
}
