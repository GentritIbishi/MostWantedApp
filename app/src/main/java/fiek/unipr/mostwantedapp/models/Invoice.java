package fiek.unipr.mostwantedapp.models;

public class Invoice {
    private String date_time, transactionID, userId, account, status;
    private Double amount;

    public Invoice() {
    }

    public Invoice(String date_time, String transactionID, String userId, String account, String status, Double amount) {
        this.date_time = date_time;
        this.transactionID = transactionID;
        this.userId = userId;
        this.account = account;
        this.status = status;
        this.amount = amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
