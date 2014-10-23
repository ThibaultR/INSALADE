package com.HKTR.INSALADE.model;

/**
 * Created by Hyukchan on 22/10/2014.
 */
public class MenuModel {
    private String date;
    private String starter;
    private String mainCourse;
    private String dessert;

    public MenuModel(String date, String starter, String mainCourse, String dessert) {
        this.date = date;
        this.starter = starter;
        this.mainCourse = mainCourse;
        this.dessert = dessert;
    }

    public String getDate() {
        return date;
    }

    public String getStarter() {
        return starter;
    }

    public String getMainCourse() {
        return mainCourse;
    }

    public String getDessert() {
        return dessert;
    }
}
