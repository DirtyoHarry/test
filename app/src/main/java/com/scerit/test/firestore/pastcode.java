package com.scerit.test.firestore;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class pastcode {
    String code;
    @ServerTimestamp Date time;


    public pastcode() {
    }

    public pastcode(String code, Date time) {
        this.code = code;
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public Date getTime() {
        return time;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
