package com.example.yamaguchi.pamsys.ReservationPassengerListFragmentHelperClasses;

/**
 * Created by yamaguchi on 2017/11/04.
 */

public class Reservation {
    public String passengerID;
    public String passengerName;
    public String getOnBusStop;
    public String getOffBusStop;
    public String note = "";

    public Reservation() {
    }

    /**
     *
     * @param passengerID
     * @param passengerName
     * @param getOnBusStop
     * @param getOffBusStop
     * @param note
     */
    public Reservation(String passengerID, String passengerName, String getOnBusStop, String getOffBusStop, String note) {
        this.passengerName = passengerName;
        this.passengerID = passengerID;
        this.getOnBusStop = getOnBusStop;
        this.getOffBusStop = getOffBusStop;
        this.note = note;
    }
}
