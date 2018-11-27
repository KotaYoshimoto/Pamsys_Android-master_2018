package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/13.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class Reservation {
    public int passengerID;
    public String passengerName;
    public String getOnBusStop ;
    public String getOffBusStop ;
    public String note;

    public Reservation(){

    }
}
