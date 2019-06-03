package com.scerit.test.firestore;

public class timeframe {
    String endtime;
    String starttime;
    String id;

    public timeframe(String endtime, String starttime, String id) {
        this.endtime = endtime;
        this.starttime = starttime;
        this.id = id;
    }

    public timeframe() {
    }

    public String getEndtime() {
        return endtime;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getId() {
        return id;
    }


}
