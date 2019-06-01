package com.scerit.test.firestore;

public class Users {
    String id;
    String email;
    Boolean booked;
    String mybike;


    public Users(String id, String email, Boolean booked, String mybike) {
        this.id = id;
        this.email = email;
        this.booked = booked;
        this.mybike = mybike;
    }

    public Users() {

    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBooked() {
        return booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public String getMybike() {
        return mybike;
    }

    public void setMybike(String mybike) {
        this.mybike = mybike;
    }
}
