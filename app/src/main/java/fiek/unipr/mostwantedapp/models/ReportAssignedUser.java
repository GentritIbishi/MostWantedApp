package fiek.unipr.mostwantedapp.models;

public class ReportAssignedUser {
    String reportAssigned_id, reportAssignUrl;

    public ReportAssignedUser() {
    }

    public ReportAssignedUser(String reportAssigned_id, String reportAssignUrl) {
        this.reportAssigned_id = reportAssigned_id;
        this.reportAssignUrl = reportAssignUrl;
    }

    public String getReportAssigned_id() {
        return reportAssigned_id;
    }

    public void setReportAssigned_id(String reportAssigned_id) {
        this.reportAssigned_id = reportAssigned_id;
    }

    public String getReportAssignUrl() {
        return reportAssignUrl;
    }

    public void setReportAssignUrl(String reportAssignUrl) {
        this.reportAssignUrl = reportAssignUrl;
    }
}
