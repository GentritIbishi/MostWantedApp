package fiek.unipr.mostwantedapp.models;

public class NotificationAdmin {
    String notificationTime, notificationBody, notificationTitle, notificationType;

    public NotificationAdmin() {
    }

    public NotificationAdmin(String notificationTime, String notificationBody, String notificationTitle, String notificationType) {
        this.notificationTime = notificationTime;
        this.notificationBody = notificationBody;
        this.notificationTitle = notificationTitle;
        this.notificationType = notificationType;
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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
