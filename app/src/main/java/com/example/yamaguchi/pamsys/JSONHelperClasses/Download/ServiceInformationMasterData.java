package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/08.
 * サーバからダウンロードされた運行情報のマスターデータ
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ServiceInformationMasterData {
    public ArrayList<String> numberplates;
    public ArrayList<String> driverNames;
    public ArrayList<Course> courses;

    public ServiceInformationMasterData() {
        // TODO 自動生成されたコンストラクター・スタブ
    }
}