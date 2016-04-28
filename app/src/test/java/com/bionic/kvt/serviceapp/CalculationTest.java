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

        assertArrayEquals( new CalculationHelper().getConditionArray("E", 0), new Integer[]{1, 1, 2, 3, 4});
        assertArrayEquals( new CalculationHelper().getConditionArray("E", 1), new Integer[]{1, 2, 3, 4, 5});
        assertArrayEquals( new CalculationHelper().getConditionArray("E", 2), new Integer[]{2, 3, 4, 5, 6});

        assertArrayEquals( new CalculationHelper().getConditionArray("S", 0), new Integer[]{1, 1, 1, 2, 3});
        assertArrayEquals( new CalculationHelper().getConditionArray("S", 1), new Integer[]{1, 1, 2, 3, 4});
        assertArrayEquals( new CalculationHelper().getConditionArray("S", 2), new Integer[]{1, 2, 3, 4, 5});

        assertArrayEquals( new CalculationHelper().getConditionArray("G", 0), new Integer[]{1, 1, 1, 1, 2});
        assertArrayEquals( new CalculationHelper().getConditionArray("G", 1), new Integer[]{1, 1, 1, 2, 3});
        assertArrayEquals( new CalculationHelper().getConditionArray("G", 2), new Integer[]{1, 1, 2, 3, 4});
    }

}
