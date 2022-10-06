package fiek.unipr.mostwantedapp.models;

public class PaymentInformation {
    private String userID,
            fullName,
            address,
            bankName,
            accountNumber,
            paypalEmail,
            paymentMethod;

    public PaymentInformation() {
    }

    public PaymentInformation(String userID, String fullName, String address, String bankName, String accountNumber, String paypalEmail, String paymentMethod) {
        this.userID = userID;
        this.fullName = fullName;
        this.address = address;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.paypalEmail = paypalEmail;
        this.paymentMethod = paymentMethod;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
