package com.scerit.test.firestore;

public class Bikes {
    String newcode;
    String oldcode;
    Boolean istaken;
    String user;
    String id;

    public Bikes() {
    }

    public Bikes(String newcode, String oldcode, Boolean istaken, String user, String id) {
        this.newcode = newcode;
        this.oldcode = oldcode;
        this.istaken = istaken;
        this.user = user;
        this.id = id;
    }

    public String getNewcode() {
        return newcode;
    }

    public String getOldcode() {
        return oldcode;
    }

    public Boolean getIstaken() {
        return istaken;
    }

    public void setNewcode(String newcode) {
        this.newcode = newcode;
    }

    public void setOldcode(String oldcode) {
        this.oldcode = oldcode;
    }

    public void setIstaken(Boolean istaken) {
        this.istaken = istaken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
