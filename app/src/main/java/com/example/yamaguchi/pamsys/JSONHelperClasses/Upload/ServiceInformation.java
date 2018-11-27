package com.example.yamaguchi.pamsys.JSONHelperClasses.Upload;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInformation {
	public String datetime;
	public String numberplate;
	public String driverName;
	public int startDistance;
	public int endDistance;
	public PassengerInformation passengerInfomation;

	public ServiceInformation() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void serviceInformationToJson(Context context, String path) {
		//debug
		Log.d("execute", " serviceInformationToJson()");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
		File file = new File(context.getFilesDir(), path);
		try {
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
