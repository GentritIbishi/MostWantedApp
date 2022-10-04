package fiek.unipr.mostwantedapp.models;

public class PaymentInformation {
    private String userID,
            fullName,
            address,
            bankName,
            paypalEmail,
            paymentMethod;
    private Integer accountNumber;

    public PaymentInformation(String userID, String fullName, String address, String bankName, String paypalEmail, String paymentMethod, Integer accountNumber) {
        this.userID = userID;
        this.fullName = fullName;
        this.address = address;
        this.bankName = bankName;
        this.paypalEmail = paypalEmail;
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }
}
