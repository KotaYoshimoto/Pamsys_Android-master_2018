package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/13.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class Reservations {
    public Route route;
    public ArrayList<Reservation> reservations = new ArrayList<Reservation>();

    public Reservations(){

    }
}