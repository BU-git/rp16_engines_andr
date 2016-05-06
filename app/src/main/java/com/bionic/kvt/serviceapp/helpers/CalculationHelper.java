package com.bionic.kvt.serviceapp.helpers;

import android.util.Log;

import com.bionic.kvt.serviceapp.models.DefectState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/** */
public enum CalculationHelper {
    INSTANCE;
    private static final Integer [] CALCULATIONARRAY = {1, 1, 1, 1, 2, 3, 4, 5, 6};
    private static final Double [] CONDITIONARRAY = {1d, 1d, 1.02, 1.1, 1.3, 1.7, 2d};

    public static final String TAG = CalculationHelper.class.getName();

    private static final Integer ROWSIZE = 5;

    CalculationHelper() {}

    public Integer [] getConditionArray(String defectType, Integer intensityPosition){
        Integer initialIndex;
        if (defectType.equals("E")) initialIndex = 2;
        else if (defectType.equals("S")) initialIndex = 1;
        else if (defectType.equals("G")) initialIndex = 0;
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

    public Integer getScoreByPart (List<DefectState> defectStateList, String part) {
        Iterator<DefectState> defectStateIterator = defectStateList.iterator();
        List<DefectState> partDefects = new ArrayList<>();
        while (defectStateIterator.hasNext()) {
            DefectState ds = defectStateIterator.next();
            if (ds.getPart().equals(part)) {
                partDefects.add(ds);
            }
        }
        Integer score;
        if (partDefects.size() > 1){
            return Collections.max(partDefects, new Comparator<DefectState>() {
                @Override
                public int compare(DefectState lhs, DefectState rhs) {
                    Log.d(TAG, "First Object: " + lhs.toString());
                    Log.d(TAG, "Second Object: " + rhs.toString());
                    return lhs.getCondition() - rhs.getCondition();
                }
            }).getCondition();
        }

        else if (partDefects.size() == 1) return partDefects.get(0).getCondition();
        else return 1;
    }
}
