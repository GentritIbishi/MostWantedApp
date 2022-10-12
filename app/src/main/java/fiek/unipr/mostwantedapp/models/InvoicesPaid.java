package fiek.unipr.mostwantedapp.models;

public class InvoicesPaid {
    private String id;
    private String [][] invoices;

    public InvoicesPaid() {
    }

    public InvoicesPaid(String id, String[][] invoices) {
        this.id = id;
        this.invoices = invoices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[][] getInvoices() {
        return invoices;
    }

    public void setInvoices(String[][] invoices) {
        this.invoices = invoices;
    }
}
