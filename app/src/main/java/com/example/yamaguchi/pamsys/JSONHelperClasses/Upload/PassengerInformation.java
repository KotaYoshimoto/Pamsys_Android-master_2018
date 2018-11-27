package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import java.util.ArrayList;

import com.example.yamaguchi.pamsys.CouponTicketSaleActivityHelperClasses.CouponTicketType;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Course;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Route;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerInformation {
    public Course course;
    public ArrayList<PassengerInformationsOfRoute> passengerInfomationsOfRoutes;
    public Detail fareCommonAdult;
    public Detail fareCommonChild;
    public Detail fareHandicappedAdult;
    public Detail fareHandicappedChild;
    public Detail totalCash;
    public Detail couponTicketCommonAdult;
    public Detail couponTicketCommonChild;
    public Detail couponTicketCommonHandicapped;
    public Detail totalCouponTicket;
    public Detail couponSoldResultAdult;
    public Detail couponSoldResultChild;
    public Detail couponSoldResultHandicapped;
    public Detail totalCouponTicketSold;
    public Detail sumSales;

    public PassengerInformation() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    /**
     * コースが決定した場合に，コース情報をすべて格納するためのコンストラクタ
     *
     * @param course
     */
    public PassengerInformation(Course course) {
        this.course = course;
        this.passengerInfomationsOfRoutes = new ArrayList<PassengerInformationsOfRoute>();
        for (Route route : course.routeList) {
            this.passengerInfomationsOfRoutes.add(new PassengerInformationsOfRoute(route));
        }
    }

    public Course getCourse() {
        return course;
    }

    /**
     *
     */
    public void calculate() {
        if (passengerInfomationsOfRoutes != null) {
            //fareCommonAdult;
            int count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.commonAdult;
                    this.fareCommonAdult = new Detail(count, 200);
                }
            }
            //fareCommonChild;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.commonChild;
                    this.fareCommonChild = new Detail(count, 100);
                }
            }

            //fareHandicappedAdult;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.handicappedAdult;
                    this.fareHandicappedAdult = new Detail(count, 100);
                }
            }

            //fareHandicappedChild;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.handicappedChild;
                    this.fareHandicappedChild = new Detail(count, 50);
                }
            }

            //totalCash;
            this.totalCash = new Detail();
            totalCash.count = fareCommonAdult.count + fareCommonChild.count + fareHandicappedAdult.count + fareHandicappedChild.count;
            totalCash.totalCost = fareCommonAdult.totalCost + fareCommonChild.totalCost + fareHandicappedAdult.totalCost + fareHandicappedChild.totalCost;

            //couponTicketCommonAdult;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.couponAdult;
                    this.couponTicketCommonAdult = new Detail(count, 200);
                }
            }

            //couponTicketCommonChild;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.couponChild;
                    this.couponTicketCommonChild = new Detail(count, 100);
                }
            }

            //couponTicketCommonHandicapped;
            count = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    count += busStop.numberOfGetOnPassenger.couponHandicapped;
                    this.couponTicketCommonHandicapped = new Detail(count, 100);
                }
            }

            //totalCouponTicket;
            this.totalCouponTicket = new Detail();
            totalCouponTicket.count = couponTicketCommonAdult.count + couponTicketCommonChild.count + couponTicketCommonHandicapped.count;
            totalCouponTicket.totalCost = couponTicketCommonAdult.totalCost + couponTicketCommonChild.totalCost + couponTicketCommonHandicapped.totalCost;

            //couponSoldResult;
            int adult = 0;
            int child = 0;
            int handicapped = 0;
            for (PassengerInformationsOfRoute route : passengerInfomationsOfRoutes) {
                for (PassengerInfomationsOfBusStop busStop : route.passengerInfomationsOfBusStops) {
                    if (busStop.couponSoldInformations != null) {
                        for (CouponSoldInformation information : busStop.couponSoldInformations) {
                            if (information.ticketType.equals(CouponTicketType.Adult.toString())) {
                                adult++;
                            } else if (information.ticketType.equals(CouponTicketType.Child.toString())) {
                                child++;
                            } else if (information.ticketType.equals(CouponTicketType.Handicapped.toString())) {
                                handicapped++;
                            }
                        }
                    }
                    continue;
                }
            }
            this.couponSoldResultAdult = new Detail(adult, 2000);
            this.couponSoldResultChild = new Detail(child, 1000);
            this.couponSoldResultHandicapped = new Detail(handicapped, 1000);

            //totalCouponTicketSold;
            this.totalCouponTicketSold = new Detail();
            this.totalCouponTicketSold.count = this.couponSoldResultAdult.count + this.couponSoldResultChild.count + this.couponSoldResultHandicapped.count;
            this.totalCouponTicketSold.totalCost = this.couponSoldResultAdult.totalCost + this.couponSoldResultChild.totalCost + this.couponSoldResultHandicapped.totalCost;

            //sumSales;
            this.sumSales = new Detail();
            sumSales.count = totalCash.count + totalCouponTicket.count + totalCouponTicketSold.count;
            sumSales.totalCost = totalCash.totalCost + totalCouponTicket.totalCost + totalCouponTicketSold.totalCost;
        }
    }

}
