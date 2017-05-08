package com.example.simbon.lefthomenotification;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Simbon on 2017/5/8.
 */

public class ConfigureSave {
    private static final String SHARED_PREFERENCES_NAME = "wifi_name";
    private static final String WIFI_NAME_KEY = "wifi_name";
    private static final String WIFI_MAX_THRESHOLD_KEY = "max_threshold";
    private static final String WIFI_MIN_THRESHOLD_KEY = "min_threshold";

    private SharedPreferences sharedPreferences;

    private String wifiName;
    private int maxThreshold;
    private int minThreshold;

    public ConfigureSave(Context context) {
        this.sharedPreferences = context.getSharedPreferences(this.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.wifiName = this.sharedPreferences.getString(this.WIFI_NAME_KEY, null);
        this.maxThreshold = this.sharedPreferences.getInt(this.WIFI_MAX_THRESHOLD_KEY, Integer.MIN_VALUE);
        this.maxThreshold = this.sharedPreferences.getInt(this.WIFI_MIN_THRESHOLD_KEY, Integer.MIN_VALUE);
    }

    public boolean isFirstLaunch() {
        if (this.wifiName == null || this.maxThreshold < 0 || this.maxThreshold < 0)
            return true;
        else
            return false;
    }

    public void saveWifiNameAndRssi(String wifiName, int maxThreshold, int minThreshold) {
        this.wifiName = wifiName;
        this.maxThreshold = maxThreshold;
        this.minThreshold = minThreshold;

        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(this.WIFI_NAME_KEY, wifiName);
        editor.putInt(this.WIFI_MAX_THRESHOLD_KEY, maxThreshold);
        editor.putInt(this.WIFI_MIN_THRESHOLD_KEY, minThreshold);
        editor.commit();
    }

    public void clearConfiguration() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getWifiName() {
        return this.wifiName;
    }

    public int getMaxThreshold() {
        return this.maxThreshold;
    }

    public int getMinThreshold() {
        return this.minThreshold;
    }
}
