package fiek.unipr.mostwantedapp.models;

import java.util.List;

public class InvoicesPaid {
    private String id;
    private List<String> invoices;

    public InvoicesPaid() {
    }

    public InvoicesPaid(String id, List<String> invoices) {
        this.id = id;
        this.invoices = invoices;
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
}
