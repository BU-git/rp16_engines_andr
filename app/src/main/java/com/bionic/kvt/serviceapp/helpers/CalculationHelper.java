package com.bionic.kvt.serviceapp.helpers;

import com.bionic.kvt.serviceapp.GlobalConstants;
import com.bionic.kvt.serviceapp.models.DefectState;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class represents the logic for Score Calculation
 * in the Default Template.
 * Implemented as Singleton.
 */
public enum CalculationHelper {
    INSTANCE;
    public static final String TAG = CalculationHelper.class.getName();
    private static final Integer[] CALCULATIONARRAY = {1, 1, 1, 1, 2, 3, 4, 5, 6};
    private static final Double[] CONDITIONARRAY = {1d, 1d, 1.02, 1.1, 1.3, 1.7, 2d};
    private static final Integer ROWSIZE = 5;

    CalculationHelper() {
    }

    /**
     * Method is used to get the sub-list of Condition array
     *
     * @param defectType
     * @param intensityPosition
     * @return Condition Array
     */
    public Integer[] getConditionArray(String defectType, Integer intensityPosition) {
        Integer initialIndex;
        switch (defectType) {
            case "E":
                initialIndex = 2;
                break;
            case "S":
                initialIndex = 1;
                break;
            case "G":
                initialIndex = 0;
                break;
            default:
                initialIndex = -1;
                break;
        }

        return getArrayByIntensityPositionAndIndex(intensityPosition, initialIndex);
    }

    /**
     * Method is used to retrieve a sub-list of 5 Integers
     * from Calculation array by moving the "window"
     * according to the Intensity position
     *
     * @param intensityPosition
     * @param initialIndex
     * @return
     */
    private Integer[] getArrayByIntensityPositionAndIndex(Integer intensityPosition, Integer initialIndex) {
        if (initialIndex >= 0) {
            switch (intensityPosition) {
                case 0:
                    return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex, initialIndex + ROWSIZE);
                case 1:
                    return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex + 1, ++initialIndex + ROWSIZE);
                case 2:
                    return Arrays.copyOfRange(CALCULATIONARRAY, initialIndex + 2, ++initialIndex + 1 + ROWSIZE);
            }
        }
        return new Integer[5];
    }

    /**
     * Method to get Condition from Condition Array
     * based on matrix calculation
     *
     * @param scopeId
     * @param intensityId
     * @param isFixed
     * @param defectType
     * @return
     */
    public Integer getCondition(Integer scopeId, Integer intensityId, Boolean isFixed, String defectType) {
        return (isFixed) ? 1 : getConditionArray(defectType, intensityId)[scopeId];
    }

    public Double getConditionFactor(Integer condition) {
        return CONDITIONARRAY[condition];
    }

    /**
     * Method to retrieve score by part.
     *
     * @param defectStateList
     * @param part
     * @return
     */
    public Integer getScoreByPart(List<DefectState> defectStateList, String part) {
        List<DefectState> partDefects = getDefectsByPart(defectStateList, part);

        if (partDefects != null && partDefects.size() > 0) {
            return getMaxDefect(partDefects).getCondition();
        } else return 1;
    }

    /**
     * Filtering defects by Part from general defect list.
     *
     * @param defectStateList
     * @param part
     * @return
     */
    public List<DefectState> getDefectsByPart(List<DefectState> defectStateList, String part) {
        Iterator<DefectState> defectStateIterator = defectStateList.iterator();
        List<DefectState> partDefects = new ArrayList<>();
        while (defectStateIterator.hasNext()) {
            DefectState ds = defectStateIterator.next();
            if (ds.getPart().equals(part)) {
                partDefects.add(ds);
            }
        }
        return partDefects;
    }

    /**
     * Method to get defect with maximum score.
     * Note: partDefects should contain values for the particular part - see getDefectsByPart method;
     *
     * @param partDefects
     * @return
     */
    public DefectState getMaxDefect(List<DefectState> partDefects) {
        if (partDefects.size() > 1) {
            return Collections.max(partDefects, new Comparator<DefectState>() {
                @Override
                public int compare(DefectState lhs, DefectState rhs) {
                    return lhs.getCondition() - rhs.getCondition();
                }
            });
        } else if (partDefects.size() == 1) return partDefects.get(0);
        else return null;
    }

    /**
     * Method to calculate the main sum of correlated score parts
     * divided by the Default raw score.
     *
     * @param partMap
     * @param defectStateList
     * @return
     */
    public Integer getGeneralScore(Map<String, LinkedHashMap<String, JsonObject>> partMap, List<DefectState> defectStateList) {
        Double sum = 0.0d;
        for (String part : partMap.keySet()) {
            List<DefectState> defectsForPart = getDefectsByPart(defectStateList, part);
            if (defectsForPart != null && defectsForPart.size() > 0) {
                DefectState maxDefect = getMaxDefect(defectsForPart);
                sum += maxDefect.getCorrelatedScore() - maxDefect.getInitialScore();
            }
        }
        return getResultForScore(sum / GlobalConstants.DEFAULT_RAW_SCORE);
    }

    /**
     * Method to get the main score by general Score.
     *
     * @param result
     * @return
     */
    public Integer getResultForScore(Double result) {
        if (result < GlobalConstants.ONE_CONDITION) return 1;
        else if (GlobalConstants.ONE_CONDITION <= result && result < GlobalConstants.TWO_CONDITION)
            return 2;
        else if (GlobalConstants.TWO_CONDITION <= result && result < GlobalConstants.THREE_CONDITION)
            return 3;
        else if (GlobalConstants.THREE_CONDITION <= result && result < GlobalConstants.FOUR_CONDITION)
            return 4;
        else if (GlobalConstants.FOUR_CONDITION <= result && result < GlobalConstants.FIVE_CONDITION)
            return 5;
        else return 6;
    }


}
