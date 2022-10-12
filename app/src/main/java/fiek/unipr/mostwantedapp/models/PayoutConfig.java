package fiek.unipr.mostwantedapp.models;

public class PayoutConfig {
    String id;
    boolean payment_state;
    Integer hour, minute, second, millisecond;
    String datetime_updated;

    public PayoutConfig(String id, boolean payment_state, Integer hour, Integer minute, Integer second, Integer millisecond, String datetime_updated) {
        this.id = id;
        this.payment_state = payment_state;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
        this.datetime_updated = datetime_updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPayment_state() {
        return payment_state;
    }

    public void setPayment_state(boolean payment_state) {
        this.payment_state = payment_state;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }

    public Integer getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(Integer millisecond) {
        this.millisecond = millisecond;
    }

    public String getDatetime_updated() {
        return datetime_updated;
    }

    public void setDatetime_updated(String datetime_updated) {
        this.datetime_updated = datetime_updated;
    }
}
