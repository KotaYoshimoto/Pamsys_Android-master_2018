package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NumberOfGetOffPassenger {
	public int commonAdult;
	public int commonChild;
	public int handicappedAdult;
	public int handicappedChild;
	public int couponAdult;
	public int couponChild;
	public int couponHandicapped;
	public int commutarPass;

	public NumberOfGetOffPassenger() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
}
