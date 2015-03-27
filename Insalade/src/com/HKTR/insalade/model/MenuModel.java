package com.HKTR.INSALADE.model;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
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

    public boolean isClosed() {
        return starter.trim().length() < 3 || mainCourse.trim().length() < 3 || dessert.trim().length() < 3;
    }
}
