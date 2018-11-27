package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/08.
 * サーバからダウンロードされた路線の情報
 */

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Route {
    public String name;
    public String serviceType;
    public String carType;
    public int courseNumber;
    public int routeNumber;
    public boolean isServiceWork;
    public String startTime;
    public ArrayList<BusStop> busStopList;

    public Route() {}

    public Route(String name, String serverType, String carType, int courseNumber, int routeNumber,
                 boolean isServiceWork, String startTime) {
        this.name = name;
        this.serviceType = serverType;
        this.carType = carType;
        this.courseNumber = courseNumber;
        this.routeNumber = routeNumber;
        this.isServiceWork = isServiceWork;
        this.startTime = startTime;
    }

    public Route(String name, String serverType, String carType, int courseNumber, int routeNumber,
                 boolean isServiceWork, String startTime,
                 ArrayList<BusStop> busStops) {
        this(name, serverType, carType, courseNumber, routeNumber, isServiceWork, startTime);
        this.busStopList = busStops;
    }

}
