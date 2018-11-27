package com.example.yamaguchi.pamsys.AnFinishServiceResultActivityHelperClasses;

import android.util.Log;

import com.example.yamaguchi.pamsys.CouponTicketSaleActivityHelperClasses.CouponTicketType;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.CouponSoldInformation;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInfomationsOfBusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Upload.PassengerInformationsOfRoute;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yamaguchi on 2017/11/17.
 */

public class Calculation {
    ArrayList<PassengerInfomationsOfBusStop> passengerInfomationsOfBusStops;

    public Calculation(ArrayList<PassengerInfomationsOfBusStop> passengerInfomationsOfBusStops) {
        this.passengerInfomationsOfBusStops = passengerInfomationsOfBusStops;
    }

    //region 乗車人数集計
    public int getTotalPassengerCommonAdult() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.commonAdult;
        }
        return sum;
    }

    public int getTotalPassengerCommonChild() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.commonChild;
        }
        return sum;
    }

    public int getTotalPassengerHandicappedAdult() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.handicappedAdult;
        }
        return sum;
    }

    public int getTotalPassengerHandicappedChild() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.handicappedChild;
        }
        return sum;
    }

    public int getTotalCouponTicketAdult() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.couponAdult;
        }
        return sum;
    }

    public int getTotalCouponTicketChild() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.couponChild;
        }
        return sum;
    }

    public int getTotalCouponTicketHandicapped() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.couponHandicapped;
        }
        return sum;
    }

    public int getTotalCommuterPass() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            sum += item.numberOfGetOnPassenger.commutarPass;
        }
        return sum;
    }

    public int getTotalPassenger() {
        int sum = getTotalPassengerCommonAdult() +
                getTotalPassengerCommonChild() +
                getTotalPassengerHandicappedAdult() +
                getTotalPassengerHandicappedChild() +
                getTotalCouponTicketAdult() +
                getTotalCouponTicketChild() +
                getTotalCouponTicketHandicapped() +
                getTotalCommuterPass();
        return sum;
    }
    //endregion

    //region 運賃集計
    public int getTotalCashCommonAdult() {
        return getTotalPassengerCommonAdult() * 200;
    }

    public int getTotalCashCommonChild() {
        return getTotalPassengerCommonChild() * 100;
    }

    public int getTotalCashHandicappedAdult() {
        return getTotalPassengerHandicappedAdult() * 100;
    }

    public int getTotalCashHandicappedChild() {
        return getTotalPassengerHandicappedChild() * 50;
    }

    public int getTotalCash() {
        return getTotalCashCommonAdult() + getTotalCashCommonChild() + getTotalCashHandicappedAdult() + getTotalCashHandicappedChild();
    }
    //endregion

    //region 回数券販売枚数
    public int getSoldCouponticketCountForAdult() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            try {
                for (CouponSoldInformation info : item.couponSoldInformations) {
                    if (info.ticketType.equals(CouponTicketType.Adult.toString())) {
                        sum++;
                    }
                }
            } catch (NullPointerException e) {
                Log.e(this.getClass().getName(), "getSoldCouponticketCountForAdult()", e);
            }
        }
        return sum;
    }

    public int getSoldCouponticketCountForChild() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            try {
                for (CouponSoldInformation info : item.couponSoldInformations) {
                    if (info.ticketType.equals(CouponTicketType.Child.toString())) {
                        sum++;
                    }
                }
            } catch (NullPointerException e) {
                Log.e(this.getClass().getName(), "getSoldCouponticketCountForAdult()", e);
            }
        }
        return sum;
    }

    public int getSoldCouponticketCountForHandicapped() {
        int sum = 0;
        for (PassengerInfomationsOfBusStop item : passengerInfomationsOfBusStops) {
            try {
                for (CouponSoldInformation info : item.couponSoldInformations) {
                    if (info.ticketType.equals(CouponTicketType.Handicapped.toString())) {
                        sum++;
                    }
                }
            } catch (NullPointerException e) {
                Log.e(this.getClass().getName(), "getSoldCouponticketCountForAdult()", e);
            }
        }
        return sum;
    }

    public int getSoldCouponSoldTicket() {
        return getSoldCouponticketCountForAdult() + getSoldCouponticketCountForChild() + getSoldCouponticketCountForHandicapped();
    }
    //endregion

    //region 回数券販売額
    public int getTotalSalseForAdult() {
        return getSoldCouponticketCountForAdult() * (200 * (11 - 1));
    }

    public int getTotalSalseForChild() {
        return getSoldCouponticketCountForChild() * (100 * (11 - 1));
    }

    public int getTotalSalseForHandicapped() {
        return getSoldCouponticketCountForHandicapped() * (100 * (11 - 1));
    }

    public int getTotalSalse() {
        return getTotalSalseForAdult() + getTotalSalseForChild() + getTotalSalseForHandicapped();
    }
    //endregion

}
