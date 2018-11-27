package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Detail {
	public int count = 0;
	public int unitCost = 0;
	public int totalCost = 0;

	public Detail() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Detail(int count, int unitCost){
		this.count = count;
		this.unitCost = unitCost;
		this.totalCost = count * unitCost;
	}
}
