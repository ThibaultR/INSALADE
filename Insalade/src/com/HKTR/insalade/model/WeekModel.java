package com.HKTR.insalade.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
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
        for(Integer weekNum : weekList.keySet()){
            for(DayModel d : weekList.get(weekNum).getWeek()){
                Log.w("dayNumber: ", (d.getDayNumber()).toString());
                if(d.getDayNumber() == (id/2)){
                    return d;
                }
            }
        }

        return new DayModel();
    }
}
