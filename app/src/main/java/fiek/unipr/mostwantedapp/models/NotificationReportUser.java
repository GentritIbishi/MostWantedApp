package fiek.unipr.mostwantedapp.models;

public class NotificationReportUser {
    String notificationReportDateTime, notificationReportBody, notificationReportTitle, notificationReportType,
            notificationReportStatusChangedTo, notificationReportUID, notificationDateTimeChanged;

    public NotificationReportUser() {
    }

    public NotificationReportUser(String notificationReportDateTime, String notificationReportBody, String notificationReportTitle, String notificationReportType, String notificationReportStatusChangedTo, String notificationReportUID, String notificationDateTimeChanged) {
        this.notificationReportDateTime = notificationReportDateTime;
        this.notificationReportBody = notificationReportBody;
        this.notificationReportTitle = notificationReportTitle;
        this.notificationReportType = notificationReportType;
        this.notificationReportStatusChangedTo = notificationReportStatusChangedTo;
        this.notificationReportUID = notificationReportUID;
        this.notificationDateTimeChanged = notificationDateTimeChanged;
    }

    public String getNotificationReportDateTime() {
        return notificationReportDateTime;
    }

    public void setNotificationReportDateTime(String notificationReportDateTime) {
        this.notificationReportDateTime = notificationReportDateTime;
    }

    public String getNotificationReportBody() {
        return notificationReportBody;
    }

    public void setNotificationReportBody(String notificationReportBody) {
        this.notificationReportBody = notificationReportBody;
    }

    public String getNotificationReportTitle() {
        return notificationReportTitle;
    }

    public void setNotificationReportTitle(String notificationReportTitle) {
        this.notificationReportTitle = notificationReportTitle;
    }

    public String getNotificationReportType() {
        return notificationReportType;
    }

    public void setNotificationReportType(String notificationReportType) {
        this.notificationReportType = notificationReportType;
    }

    public String getNotificationReportStatusChangedTo() {
        return notificationReportStatusChangedTo;
    }

    public void setNotificationReportStatusChangedTo(String notificationReportStatusChangedTo) {
        this.notificationReportStatusChangedTo = notificationReportStatusChangedTo;
    }

    public String getNotificationReportUID() {
        return notificationReportUID;
    }

    public void setNotificationReportUID(String notificationReportUID) {
        this.notificationReportUID = notificationReportUID;
    }

    public String getNotificationDateTimeChanged() {
        return notificationDateTimeChanged;
    }

    public void setNotificationDateTimeChanged(String notificationDateTimeChanged) {
        this.notificationDateTimeChanged = notificationDateTimeChanged;
    }
}
