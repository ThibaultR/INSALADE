package com.HKTR.INSALADE.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hyukchan on 22/10/2014.
 */
public class WeekModel {
    private static HashMap<Integer, WeekModel> weekList = new HashMap<Integer, WeekModel>();

    private static Integer currentMenuId;

    private static Boolean currentMenuIsLunch;

    private ArrayList<DayModel> week;


    public WeekModel() {
        this.week = new ArrayList<DayModel>();
    }

    public static HashMap<Integer, WeekModel> getWeekList() {
        return weekList;
    }

    public ArrayList<DayModel> getWeek() {
        return week;
    }

    public static Integer getCurrentMenuId() {
        return currentMenuId;
    }

    public static void setCurrentMenuId(Integer currentMenuId) {
        WeekModel.currentMenuId = currentMenuId;
    }


    public static Boolean getCurrentMenuIsLunch() {
        return currentMenuIsLunch;
    }

    public static void setCurrentMenuIsLunch(Boolean currentMenuIsLunch) {
        WeekModel.currentMenuIsLunch = currentMenuIsLunch;
    }

    public static DayModel getDayById(Integer id) {
        return weekList.get(42).getWeek().get(id/2); //TODO : to Generalize
    }
}
