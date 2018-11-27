package com.example.yamaguchi.pamsys.ServiceInformationSettingActivityHelperClasses;

import android.app.admin.DeviceAdminInfo;

import java.lang.annotation.Retention;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yamaguchi on 2017/11/08.
 */

public class CurrentDateTime {

    Calendar calendar;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;
    String week;
    String[] weekTable = {"日", "月", "火", "水", "木", "金", "土"};

    public CurrentDateTime() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        week = weekTable[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getSecond() {
        return this.second;
    }

    public String getWeek() {
        return this.week;
    }

    public String showDate() {
        return String.valueOf(year) + "年" + String.valueOf(month) + "月" + String.valueOf(day) + "日(" + week + ")";
    }

    public String storedDateTime() {
        return showDate() + " " + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
    }

}
