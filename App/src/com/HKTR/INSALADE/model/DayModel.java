package com.HKTR.INSALADE.model;

/**
 * Created by Hyukchan on 22/10/2014.
 */
public class DayModel {
    private MenuModel lunch;
    private MenuModel dinner;

    public MenuModel getLunch() {
        return lunch;
    }

    public MenuModel getDinner() {
        return dinner;
    }

    public void setLunch(MenuModel lunch) {
        this.lunch = lunch;
    }

    public void setDinner(MenuModel dinner) {
        this.dinner = dinner;
    }
}
