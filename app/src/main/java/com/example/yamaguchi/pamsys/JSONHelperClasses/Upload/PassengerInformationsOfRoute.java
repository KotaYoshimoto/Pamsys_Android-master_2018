package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import java.util.ArrayList;

import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.BusStop;
import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.Route;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerInformationsOfRoute {
	public Route route;
	public ArrayList<PassengerInfomationsOfBusStop> passengerInfomationsOfBusStops;
	public String note;

	public PassengerInformationsOfRoute() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PassengerInformationsOfRoute(Route route){
		this.route = route;
		this.passengerInfomationsOfBusStops = new ArrayList<PassengerInfomationsOfBusStop>();
		for(BusStop busStop : this.route.busStopList){
			this.passengerInfomationsOfBusStops.add(new PassengerInfomationsOfBusStop(busStop));
		}
	}
}
