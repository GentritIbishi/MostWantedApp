package fiek.unipr.mostwantedapp.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

import fiek.unipr.mostwantedapp.R;
import fiek.unipr.mostwantedapp.dashboard.InformerDashboardActivity;
import fiek.unipr.mostwantedapp.helpers.PersonImage;
import fiek.unipr.mostwantedapp.register.RegisterPerson;

public class Informer {
    Integer balance;
    String name, lastname, fullName, Address, Email, parentName, grade, googleID, phone, personal_number, password;
    Uri photoURL;

    public Informer() {
    }

    public Informer(Integer balance, String name, String lastname, String fullName, String address, String email, String parentName, String grade, String googleID, String phone, String personal_number, String password, Uri photoURL) {
        this.balance = balance;
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
        this.password = password;
        this.photoURL = photoURL;
    }

    public Informer(String personal_number, Integer balance, String phone, String name, String lastname, String fullName, String address, String email, String parentName, String grade, String googleID, Uri photoURL) {
        this.personal_number = personal_number;
        this.balance = balance;
        this.phone = phone;
        this.name = name;
        this.lastname = lastname;
        this.fullName = fullName;
        Address = address;
        Email = email;
        this.parentName = parentName;
        this.grade = grade;
        this.googleID = googleID;
        this.photoURL = photoURL;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Uri getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(Uri photoURL) {
        this.photoURL = photoURL;
    }
}
