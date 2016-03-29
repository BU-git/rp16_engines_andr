package com.bionic.kvt.serviceapp.models;

/*
LMRA Model Class
 */
public class LMRA {
    private String lmraName;
    private String lmraDescription;

    public LMRA (String lmraName, String lmraDescription){
        this.lmraName = lmraName;
        this.lmraDescription = lmraDescription;
    }

    public String getLmraName() {
        return lmraName;
    }

    public void setLmraName(String lmraName) {
        this.lmraName = lmraName;
    }

    public String getLmraDescription() {
        return lmraDescription;
    }

    public void setLmraDescription(String lmraDescription) {
        this.lmraDescription = lmraDescription;
    }
}
