package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by yamaguchi on 2017/11/08.
 * サーバからダウンロードされた停留所の情報
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BusStop {
    public String name;
    public String kanaName;
    public double longitude;
    public double latitude;

    public BusStop() {}

    public BusStop(String name, String kanaName) {
        // TODO 自動生成されたコンストラクター・スタブ
        this.name = name;
        this.kanaName = kanaName;
    }

    public BusStop(String name, String kanaName, int longitude, int latitude) {
        this(name, kanaName);
        this.longitude = longitude;
        this.latitude = latitude;
    }
}