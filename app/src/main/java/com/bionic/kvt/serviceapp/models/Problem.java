package com.bionic.kvt.serviceapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**

 */
public class Problem implements Parcelable, Serializable {

    private String problemName;
    private String problemScope;
    private String problemIntensity;
    private String problemAction;
    private boolean problemResolved;

    public Problem(){}

    protected Problem(Parcel in) {
        problemName = in.readString();
        problemScope = in.readString();
        problemIntensity = in.readString();
        problemAction = in.readString();
        problemResolved = in.readByte() != 0;
    }

    public static final Creator<Problem> CREATOR = new Creator<Problem>() {
        @Override
        public Problem createFromParcel(Parcel in) {
            return new Problem(in);
        }

        @Override
        public Problem[] newArray(int size) {
            return new Problem[size];
        }
    };

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemScope() {
        return problemScope;
    }

    public void setProblemScope(String problemScope) {
        this.problemScope = problemScope;
    }

    public String getProblemIntensity() {
        return problemIntensity;
    }

    public void setProblemIntensity(String problemIntensity) {
        this.problemIntensity = problemIntensity;
    }

    public String getProblemAction() {
        return problemAction;
    }

    public void setProblemAction(String problemAction) {
        this.problemAction = problemAction;
    }

    public boolean isProblemResolved() {
        return problemResolved;
    }

    public void setProblemResolved(boolean problemResolved) {
        this.problemResolved = problemResolved;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(problemName);
        dest.writeString(problemScope);
        dest.writeString(problemIntensity);
        dest.writeString(problemAction);
        dest.writeByte((byte) (problemResolved ? 1 : 0));
    }
}
