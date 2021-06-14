package fiek.unipr.mostwantedapp;

public class User {

    public String userID, fullName, email, role;

    public User() {

    }

    public User(String userID, String fullName, String email, String role) {
        this.fullName = fullName;
        this.email = email;
        this.userID = userID;
        this.role = role;
    }

    public User(String userID, String email, String role)
    {
        this.userID = userID;
        this.email = email;
        this.role = role;
    }
}
