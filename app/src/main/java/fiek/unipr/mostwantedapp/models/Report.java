package fiek.unipr.mostwantedapp.models;

public class Report {
    private String Title, Description, Date_time, user_ID;
    private Double longitude, latitude;

    public Report() {
    }

    public Report(String title, String description, String date_time, String user_ID, Double longitude, Double latitude) {
        Title = title;
        Description = description;
        Date_time = date_time;
        this.user_ID = user_ID;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate_time() {
        return Date_time;
    }

    public void setDate_time(String date_time) {
        Date_time = date_time;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
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
