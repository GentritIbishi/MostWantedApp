package fiek.unipr.mostwantedapp.models;

public class Balance {
    private String balance_user_id,
                   balance_user,
                   balance_email,
                   balance_user_type,
                   balance_unpaid_price,
                   balance_paid_price;

    public Balance(String balance_user_id, String balance_user, String balance_email, String balance_user_type, String balance_unpaid_price, String balance_paid_price) {
        this.balance_user_id = balance_user_id;
        this.balance_user = balance_user;
        this.balance_email = balance_email;
        this.balance_user_type = balance_user_type;
        this.balance_unpaid_price = balance_unpaid_price;
        this.balance_paid_price = balance_paid_price;
    }

    public Balance(String balance_user, String balance_email, String balance_user_type, String balance_unpaid_price, String balance_paid_price) {
        this.balance_user = balance_user;
        this.balance_email = balance_email;
        this.balance_user_type = balance_user_type;
        this.balance_unpaid_price = balance_unpaid_price;
        this.balance_paid_price = balance_paid_price;
    }

    public Balance() {
    }

    public String getBalance_user_id() {
        return balance_user_id;
    }

    public void setBalance_user_id(String balance_user_id) {
        this.balance_user_id = balance_user_id;
    }

    public String getBalance_user() {
        return balance_user;
    }

    public void setBalance_user(String balance_user) {
        this.balance_user = balance_user;
    }

    public String getBalance_email() {
        return balance_email;
    }

    public void setBalance_email(String balance_email) {
        this.balance_email = balance_email;
    }

    public String getBalance_user_type() {
        return balance_user_type;
    }

    public void setBalance_user_type(String balance_user_type) {
        this.balance_user_type = balance_user_type;
    }

    public String getBalance_unpaid_price() {
        return balance_unpaid_price;
    }

    public void setBalance_unpaid_price(String balance_unpaid_price) {
        this.balance_unpaid_price = balance_unpaid_price;
    }

    public String getBalance_paid_price() {
        return balance_paid_price;
    }

    public void setBalance_paid_price(String balance_paid_price) {
        this.balance_paid_price = balance_paid_price;
    }
}
