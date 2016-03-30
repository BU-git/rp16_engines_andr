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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LMRA lmra = (LMRA) o;

        if (lmraName != null ? !lmraName.equals(lmra.lmraName) : lmra.lmraName != null)
            return false;
        return !(lmraDescription != null ? !lmraDescription.equals(lmra.lmraDescription) : lmra.lmraDescription != null);

    }

    @Override
    public int hashCode() {
        int result = lmraName != null ? lmraName.hashCode() : 0;
        result = 31 * result + (lmraDescription != null ? lmraDescription.hashCode() : 0);
        return result;
    }
}
