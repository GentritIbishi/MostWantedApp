package fiek.unipr.mostwantedapp.models;

public class ReportAssigned {
    private String reportAssigned_id, first_investigator, second_investigator, fullNameOfWantedPerson;
    private String last_seen_address, first_address, second_address, third_address, forth_address, date;

    public ReportAssigned(String reportAssigned_id, String first_investigator, String second_investigator, String fullNameOfWantedPerson, String last_seen_address, String first_address, String second_address, String third_address, String forth_address, String date) {
        this.reportAssigned_id = reportAssigned_id;
        this.first_investigator = first_investigator;
        this.second_investigator = second_investigator;
        this.fullNameOfWantedPerson = fullNameOfWantedPerson;
        this.last_seen_address = last_seen_address;
        this.first_address = first_address;
        this.second_address = second_address;
        this.third_address = third_address;
        this.forth_address = forth_address;
        this.date = date;
    }

    public String getReportAssigned_id() {
        return reportAssigned_id;
    }

    public void setReportAssigned_id(String reportAssigned_id) {
        this.reportAssigned_id = reportAssigned_id;
    }

    public String getFirst_investigator() {
        return first_investigator;
    }

    public void setFirst_investigator(String first_investigator) {
        this.first_investigator = first_investigator;
    }

    public String getSecond_investigator() {
        return second_investigator;
    }

    public void setSecond_investigator(String second_investigator) {
        this.second_investigator = second_investigator;
    }

    public String getFullNameOfWantedPerson() {
        return fullNameOfWantedPerson;
    }

    public void setFullNameOfWantedPerson(String fullNameOfWantedPerson) {
        this.fullNameOfWantedPerson = fullNameOfWantedPerson;
    }

    public String getLast_seen_address() {
        return last_seen_address;
    }

    public void setLast_seen_address(String last_seen_address) {
        this.last_seen_address = last_seen_address;
    }

    public String getFirst_address() {
        return first_address;
    }

    public void setFirst_address(String first_address) {
        this.first_address = first_address;
    }

    public String getSecond_address() {
        return second_address;
    }

    public void setSecond_address(String second_address) {
        this.second_address = second_address;
    }

    public String getThird_address() {
        return third_address;
    }

    public void setThird_address(String third_address) {
        this.third_address = third_address;
    }

    public String getForth_address() {
        return forth_address;
    }

    public void setForth_address(String forth_address) {
        this.forth_address = forth_address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
