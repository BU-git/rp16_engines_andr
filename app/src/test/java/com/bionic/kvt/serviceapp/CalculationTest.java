package com.bionic.kvt.serviceapp;

import android.util.Log;

import org.junit.Test;
import com.bionic.kvt.serviceapp.helpers.CalculationHelper;

import java.util.Arrays;
import java.util.StringTokenizer;

import static org.junit.Assert.*;

/**
 * Test to verify the matrix calculations
 * */
public class CalculationTest {
    public static final String TAG = CalculationTest.class.getName();

    @Test
    public void validateConditionArray(){
        String[] conditionArray = {"E", "S", "G"};

        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("E", 0), new Integer[]{1, 1, 2, 3, 4});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("E", 1), new Integer[]{1, 2, 3, 4, 5});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("E", 2), new Integer[]{2, 3, 4, 5, 6});

        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("S", 0), new Integer[]{1, 1, 1, 2, 3});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("S", 1), new Integer[]{1, 1, 2, 3, 4});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("S", 2), new Integer[]{1, 2, 3, 4, 5});

        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("G", 0), new Integer[]{1, 1, 1, 1, 2});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("G", 1), new Integer[]{1, 1, 1, 2, 3});
        assertArrayEquals( CalculationHelper.INSTANCE.getConditionArray("G", 2), new Integer[]{1, 1, 2, 3, 4});
    }

}
