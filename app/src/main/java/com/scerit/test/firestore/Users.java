package com.scerit.test.firestore;

public class Users {
    String id;
    String email;
    Boolean booked;
    String mybike;
    String cbookingid;

    public Users(String id, String email, Boolean booked, String mybike, String cbookingid) {
        this.id = id;
        this.email = email;
        this.booked = booked;
        this.mybike = mybike;
        this.cbookingid = cbookingid;
    }

    public Users() {
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getBooked() {
        return booked;
    }

    public String getMybike() {
        return mybike;
    }

    public String getCbookingid() {
        return cbookingid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public void setMybike(String mybike) {
        this.mybike = mybike;
    }

    public void setCbookingid(String cbookingid) {
        this.cbookingid = cbookingid;
    }
}
