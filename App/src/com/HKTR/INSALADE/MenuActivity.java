package com.HKTR.INSALADE;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;

/**
 * Created by Hyukchan on 23/10/2014.
 */
public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        /*TextView tv = (TextView) findViewById(R.id.textView);

        DayModel currentDay = WeekModel.getWeekList().get(42).getWeek().get(WeekModel.getCurrentMenuId());
        MenuModel currentMenu;

        if( WeekModel.getCurrentMenuIsLunch()) {
            currentMenu = currentDay.getLunch();
            tv.append(currentMenu.getDate() + " midi\n");
        } else {
            currentMenu = currentDay.getDinner();
            tv.append(currentMenu.getDate() + " soir\n");
        }
        tv.append(currentMenu.getStarter() + "\n\n");
        tv.append(currentMenu.getMainCourse() + "\n\n");
        tv.append(currentMenu.getDessert() + "\n");*/
    }
}