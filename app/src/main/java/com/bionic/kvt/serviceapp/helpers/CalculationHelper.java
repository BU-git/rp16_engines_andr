package com.bionic.kvt.serviceapp.helpers;

import java.util.Arrays;
import java.util.List;

/** */
public class CalculationHelper {
    private static final Integer [] CALCULATIONARRAY = {1, 1, 1, 1, 2, 3, 4, 5, 6};
    private static final Double [] CONDITIONARRAY = {1d, 1d, 1.02, 1.1, 1.3, 1.7, 2d};

    private static final Integer ROWSIZE = 5;

    public CalculationHelper() {}

    public Integer [] getConditionArray(String defectType, Integer intensityPosition){
        Integer initialIndex;
        if (defectType == "E") initialIndex = 2;
        else if (defectType == "S") initialIndex = 1;
        else if (defectType == "G") initialIndex = 0;
        else initialIndex = -1;

        return getArrayByIntensityPositionAndIndex(intensityPosition, initialIndex);
    }

    private Integer [] getArrayByIntensityPositionAndIndex(Integer intensityPosition, Integer initialIndex) {
        if (initialIndex >= 0){
            switch (intensityPosition) {
                case 0: return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex, initialIndex + ROWSIZE);
                case 1: return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex + 1, ++initialIndex + ROWSIZE);
                case 2: return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex + 2, ++initialIndex + 1 + ROWSIZE);
            }
        }
        return new Integer[5];
    }

    public Integer getCondition (Integer scopeId, Integer intensityId, String defectType){
        return getConditionArray(defectType, intensityId)[scopeId];
    }

    public Double getConditionFactor (Integer condition){
        return CONDITIONARRAY[condition];
    }
}
