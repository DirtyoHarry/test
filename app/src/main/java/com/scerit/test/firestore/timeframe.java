package com.scerit.test.firestore;

public class timeframe {
    String endtime;
    String starttime;

    public timeframe(String endtime, String starttime) {
        this.endtime = endtime;
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getStarttime() {
        return starttime;
    }
}
