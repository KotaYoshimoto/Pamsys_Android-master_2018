package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import java.util.ArrayList;

import com.example.yamaguchi.pamsys.JSONHelperClasses.Download.BusStop;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerInfomationsOfBusStop {
	public BusStop busStop;
	public NumberOfGetOnPassenger numberOfGetOnPassenger = new NumberOfGetOnPassenger();
	public NumberOfGetOffPassenger numberOfGetOffPassenger = new NumberOfGetOffPassenger();
	public int totalNumberOfGetOffPassenger;
	public ArrayList<CouponSoldInformation> couponSoldInformations = new ArrayList<CouponSoldInformation>();

	public PassengerInfomationsOfBusStop() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PassengerInfomationsOfBusStop(BusStop busStop){
		this.busStop = busStop;
	}

	@Override
	public String toString(){
		return this.busStop.name;
	}
}
