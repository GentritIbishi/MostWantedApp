package fiek.unipr.mostwantedapp.models;

public class NotificationAdmin {
    String notificationTime, notificationBody, notificationTitle;

    public NotificationAdmin() {
    }

    public NotificationAdmin(String notificationTime, String notificationBody, String notificationTitle) {
        this.notificationTime = notificationTime;
        this.notificationBody = notificationBody;
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }
}
