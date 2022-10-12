package fiek.unipr.mostwantedapp.models;

import java.util.List;

public class InvoicesPaid {
    private String id;
    private List<String> invoices;
    private String date_time;

    public InvoicesPaid() {
    }

    public InvoicesPaid(String id, List<String> invoices, String date_time) {
        this.id = id;
        this.invoices = invoices;
        this.date_time = date_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<String> invoices) {
        this.invoices = invoices;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
