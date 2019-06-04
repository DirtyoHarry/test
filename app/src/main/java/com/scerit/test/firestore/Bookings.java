package com.scerit.test.firestore;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Bookings {
    String bike;
    String timeframe;
    String today;
    Boolean active;


    public Bookings(String bike, String timeframe, String today, Boolean active) {
        this.bike = bike;
        this.timeframe = timeframe;
        this.today = today;
        this.active = active;
    }

    public Bookings() {
    }

    public String getBike() {
        return bike;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setBike(String bike) {
        this.bike = bike;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
