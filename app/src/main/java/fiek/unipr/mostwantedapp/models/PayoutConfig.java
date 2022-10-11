package fiek.unipr.mostwantedapp.models;

public class PayoutConfig {
    String ID;
    boolean PAYMENT_STATE;
    int HOUR, MINUTE, SECOND, MILLISECOND;
    String DATETIME_UPDATED;

    public PayoutConfig(String ID, boolean PAYMENT_STATE, int HOUR, int MINUTE, int SECOND, int MILLISECOND, String DATETIME_UPDATED) {
        this.ID = ID;
        this.PAYMENT_STATE = PAYMENT_STATE;
        this.HOUR = HOUR;
        this.MINUTE = MINUTE;
        this.SECOND = SECOND;
        this.MILLISECOND = MILLISECOND;
        this.DATETIME_UPDATED = DATETIME_UPDATED;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isPAYMENT_STATE() {
        return PAYMENT_STATE;
    }

    public void setPAYMENT_STATE(boolean PAYMENT_STATE) {
        this.PAYMENT_STATE = PAYMENT_STATE;
    }

    public int getHOUR() {
        return HOUR;
    }

    public void setHOUR(int HOUR) {
        this.HOUR = HOUR;
    }

    public int getMINUTE() {
        return MINUTE;
    }

    public void setMINUTE(int MINUTE) {
        this.MINUTE = MINUTE;
    }

    public int getSECOND() {
        return SECOND;
    }

    public void setSECOND(int SECOND) {
        this.SECOND = SECOND;
    }

    public int getMILLISECOND() {
        return MILLISECOND;
    }

    public void setMILLISECOND(int MILLISECOND) {
        this.MILLISECOND = MILLISECOND;
    }

    public String getDATETIME_UPDATED() {
        return DATETIME_UPDATED;
    }

    public void setDATETIME_UPDATED(String DATETIME_UPDATED) {
        this.DATETIME_UPDATED = DATETIME_UPDATED;
    }
}
