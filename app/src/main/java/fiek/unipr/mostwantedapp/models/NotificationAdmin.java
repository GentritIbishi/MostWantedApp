package fiek.unipr.mostwantedapp.models;

public class NotificationAdmin {
    String notificationTime, notificationBody, notificationTitle, notificationBodySub;

    public NotificationAdmin() {
    }

    public NotificationAdmin(String notificationTime, String notificationBody, String notificationTitle, String notificationBodySub) {
        this.notificationTime = notificationTime;
        this.notificationBody = notificationBody;
        this.notificationTitle = notificationTitle;
        this.notificationBodySub = notificationBodySub;
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

    public String getNotificationBodySub() {
        return notificationBodySub;
    }

    public void setNotificationBodySub(String notificationBodySub) {
        this.notificationBodySub = notificationBodySub;
    }
}
