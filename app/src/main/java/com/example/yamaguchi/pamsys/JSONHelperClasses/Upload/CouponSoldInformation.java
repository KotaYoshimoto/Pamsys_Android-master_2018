package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponSoldInformation {
	public String ticketType;
	public int ticketNumber;

	public CouponSoldInformation() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public CouponSoldInformation(String ticketType, int ticketNumber){
		this.ticketType = ticketType;
		this.ticketNumber = ticketNumber;
	}
}
