package fiek.unipr.mostwantedapp.models;

public class Notification {
    String  notificationId,
            notificationDateTime,
            notificationType,
            notificationReportId,
            notificationReportUid,
            notificationReportDateTime,
            notificationReportTitle,
            notificationReportDescription,
            notificationReportInformerPerson,
            notificationReportWantedPerson,
            notificationReportPrizeToWin,
            notificationReportNewStatus,
            notificationForUserId;

    public Notification() {
    }

    public Notification(String notificationId, String notificationDateTime, String notificationType, String notificationReportId, String notificationReportUid, String notificationReportDateTime, String notificationReportTitle, String notificationReportDescription, String notificationReportInformerPerson, String notificationReportWantedPerson, String notificationReportPrizeToWin, String notificationReportNewStatus, String notificationForUserId) {
        this.notificationId = notificationId;
        this.notificationDateTime = notificationDateTime;
        this.notificationType = notificationType;
        this.notificationReportId = notificationReportId;
        this.notificationReportUid = notificationReportUid;
        this.notificationReportDateTime = notificationReportDateTime;
        this.notificationReportTitle = notificationReportTitle;
        this.notificationReportDescription = notificationReportDescription;
        this.notificationReportInformerPerson = notificationReportInformerPerson;
        this.notificationReportWantedPerson = notificationReportWantedPerson;
        this.notificationReportPrizeToWin = notificationReportPrizeToWin;
        this.notificationReportNewStatus = notificationReportNewStatus;
        this.notificationForUserId = notificationForUserId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(String notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationReportId() {
        return notificationReportId;
    }

    public void setNotificationReportId(String notificationReportId) {
        this.notificationReportId = notificationReportId;
    }

    public String getNotificationReportUid() {
        return notificationReportUid;
    }

    public void setNotificationReportUid(String notificationReportUid) {
        this.notificationReportUid = notificationReportUid;
    }

    public String getNotificationReportDateTime() {
        return notificationReportDateTime;
    }

    public void setNotificationReportDateTime(String notificationReportDateTime) {
        this.notificationReportDateTime = notificationReportDateTime;
    }

    public String getNotificationReportTitle() {
        return notificationReportTitle;
    }

    public void setNotificationReportTitle(String notificationReportTitle) {
        this.notificationReportTitle = notificationReportTitle;
    }

    public String getNotificationReportDescription() {
        return notificationReportDescription;
    }

    public void setNotificationReportDescription(String notificationReportDescription) {
        this.notificationReportDescription = notificationReportDescription;
    }

    public String getNotificationReportInformerPerson() {
        return notificationReportInformerPerson;
    }

    public void setNotificationReportInformerPerson(String notificationReportInformerPerson) {
        this.notificationReportInformerPerson = notificationReportInformerPerson;
    }

    public String getNotificationReportWantedPerson() {
        return notificationReportWantedPerson;
    }

    public void setNotificationReportWantedPerson(String notificationReportWantedPerson) {
        this.notificationReportWantedPerson = notificationReportWantedPerson;
    }

    public String getNotificationReportPrizeToWin() {
        return notificationReportPrizeToWin;
    }

    public void setNotificationReportPrizeToWin(String notificationReportPrizeToWin) {
        this.notificationReportPrizeToWin = notificationReportPrizeToWin;
    }

    public String getNotificationReportNewStatus() {
        return notificationReportNewStatus;
    }

    public void setNotificationReportNewStatus(String notificationReportNewStatus) {
        this.notificationReportNewStatus = notificationReportNewStatus;
    }

    public String getNotificationForUserId() {
        return notificationForUserId;
    }

    public void setNotificationForUserId(String notificationForUserId) {
        this.notificationForUserId = notificationForUserId;
    }
}
