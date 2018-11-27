package com.example.yamaguchi.pamsys.ReservationBusStopListHelperClasses;

/**
 * Created by yamaguchi on 2017/11/03.
 */

public class ReservationBusStopInformation {
    public int id;
    public String busStopName = "";
    public int willGetOnCount = 0;
    public int willGetOffCount = 0;

    public ReservationBusStopInformation(){

    }

    public ReservationBusStopInformation(int id, String busStopName){
        this.id = id;
        this.busStopName = busStopName;
    }
}
