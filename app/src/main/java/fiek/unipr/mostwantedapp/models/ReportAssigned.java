package fiek.unipr.mostwantedapp.models;

import java.util.List;

public class ReportAssigned {
    private String reportAssigned_id, first_investigator, second_investigator, fullNameOfWantedPerson, date;
    private List<String> listLocations;

    public ReportAssigned() {
    }

    public ReportAssigned(String reportAssigned_id, String first_investigator, String second_investigator, String fullNameOfWantedPerson, String date, List<String> listLocations) {
        this.reportAssigned_id = reportAssigned_id;
        this.first_investigator = first_investigator;
        this.second_investigator = second_investigator;
        this.fullNameOfWantedPerson = fullNameOfWantedPerson;
        this.date = date;
        this.listLocations = listLocations;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getListLocations() {
        return listLocations;
    }

    public void setListLocations(List<String> listLocations) {
        this.listLocations = listLocations;
    }
}
