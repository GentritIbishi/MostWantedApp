package fiek.unipr.mostwantedapp.models;

public class Invoice {
    private String created_date_time,
            transactionID,
            userId,
            account,
            fullName,
            address,
            bankName,
            accountNumber,
            paypalEmail,
            status,
            updated_date_time;
    private Double amount;

    public Invoice() {
    }

    public Invoice(String created_date_time, String transactionID, String userId, String account, String fullName, String address, String bankName, String accountNumber, String paypalEmail, String status, String updated_date_time, Double amount) {
        this.created_date_time = created_date_time;
        this.transactionID = transactionID;
        this.userId = userId;
        this.account = account;
        this.fullName = fullName;
        this.address = address;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.paypalEmail = paypalEmail;
        this.status = status;
        this.updated_date_time = updated_date_time;
        this.amount = amount;
    }

    public String getCreated_date_time() {
        return created_date_time;
    }

    public void setCreated_date_time(String created_date_time) {
        this.created_date_time = created_date_time;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_date_time() {
        return updated_date_time;
    }

    public void setUpdated_date_time(String updated_date_time) {
        this.updated_date_time = updated_date_time;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
