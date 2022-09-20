package fiek.unipr.mostwantedapp.models;

public class LoginHistory {
    String userID, email, password, role, date_time;

    public LoginHistory(String userID, String email, String password, String role, String date_time) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.role = role;
        this.date_time = date_time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
