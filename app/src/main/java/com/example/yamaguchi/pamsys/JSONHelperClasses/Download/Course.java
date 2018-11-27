package com.example.yamaguchi.pamsys.JSONHelperClasses.Download;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by yamaguchi on 2017/11/08.
 * サーバからダウンロードされたコースの情報
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Course {
    public String name;
    public int serviceNumber;
    public ArrayList<Route> routeList;

    public Course() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

}