package com.example.yamaguchi.pamsys.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.yamaguchi.pamsys.R;

/**
 * Created by yamaguchi on 2017/11/16.
 */

public class SharedPreferenceHelper {

    public static int getCurrentCourseNum(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int currentNumber = preference.getInt(context.getString(R.string.current_course_num), 1);

        return currentNumber;
    }

    public static void setCurrentCourseNum(Context context, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        preference.edit().putInt(context.getString(R.string.current_course_num), value).apply();

    }

    public static int getCurrentBusStopNum(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int currentNumber = preference.getInt(context.getString(R.string.current_busstop_num), 0);

        return currentNumber;
    }

    public static void setCurrentBusStopNum(Context context, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        preference.edit().putInt(context.getString(R.string.current_busstop_num), value).apply();

    }

    public static String getCurrentCarType(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String carType = preference.getString(context.getString(R.string.current_car_type), "定期");

        return carType;
    }

    public static void setCurrentCarType(Context context, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        preference.edit().putString(context.getString(R.string.current_car_type), value).apply();

    }

    public static String getLaterReservationDownLoaded(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String carType = preference.getString(context.getString(R.string.later_reservation_download), "");

        return carType;
    }

    public static void setLaterReservationDownLoaded(Context context, String value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        preference.edit().putString(context.getString(R.string.later_reservation_download), value).apply();

    }

    public static int getPreBusStopNum(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getInt(context.getString(R.string.pre_busstop_num), 0);
    }

    public static void setPreBusStopNum(Context context, int value) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        preference.edit().putInt(context.getString(R.string.pre_busstop_num), value).apply();
    }



}
