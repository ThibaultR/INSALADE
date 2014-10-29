package com.HKTR.INSALADE.model;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class DayModel {
    private MenuModel lunch;
    private MenuModel dinner;
    private Integer weekNumber;
    private Integer dayNumber;

    public MenuModel getLunch() {
        return lunch;
    }

    public MenuModel getDinner() {
        return dinner;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setLunch(MenuModel lunch) {
        this.lunch = lunch;
    }

    public void setDinner(MenuModel dinner) {
        this.dinner = dinner;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

}
