package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/*
LMRA Model Class
 */
public class LMRA implements Parcelable, Serializable {
    private String lmraName;
    private String lmraDescription;

    public LMRA(String lmraName, String lmraDescription) {
        this.lmraName = lmraName;
        this.lmraDescription = lmraDescription;
    }

    protected LMRA(Parcel in) {
        lmraName = in.readString();
        lmraDescription = in.readString();
    }

    public static final Creator<LMRA> CREATOR = new Creator<LMRA>() {
        @Override
        public LMRA createFromParcel(Parcel in) {
            return new LMRA(in);
        }

        @Override
        public LMRA[] newArray(int size) {
            return new LMRA[size];
        }
    };

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

    //Parcelazied implmentation.

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }
}
