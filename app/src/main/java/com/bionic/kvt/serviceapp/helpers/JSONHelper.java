package com.bionic.kvt.serviceapp.helpers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utilities for JSON conversion.
 */
public class JSONHelper {

    public String readFromFile(Context context, String filename) {
        String result = "";

        try {
            InputStream jsonInputStream = context.getAssets().open(filename);
            if (jsonInputStream != null) {
                InputStreamReader jsonInputStreamReader = new InputStreamReader(jsonInputStream);
                BufferedReader jsonBufferedReader = new BufferedReader(jsonInputStreamReader);
                StringBuilder jsonStringBuilder = new StringBuilder();
                String jsonString = "";
                while ((jsonString = jsonBufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString);
                }
                jsonInputStream.close();
                result = jsonStringBuilder.toString();
            }
        } catch (IOException e) {
            Log.e(context.getClass().getName(), e.toString());
        }

        return result;
    }
}
