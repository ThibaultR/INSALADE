package com.HKTR.INSALADE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;

import java.util.prefs.Preferences;

/**
 * Created by Hyukchan on 23/10/2014.
 */
public class MenuActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        TextView date = (TextView) findViewById(R.id.dateText);
        TextView starterContent = (TextView) findViewById(R.id.starterContent);
        TextView mainCourseContent = (TextView) findViewById(R.id.mainCourseContent);
        TextView dessertContent = (TextView) findViewById(R.id.dessertContent);

        DayModel currentDay = WeekModel.getWeekList().get(42).getWeek().get(WeekModel.getCurrentMenuId());// TODO : to generalize
        MenuModel currentMenu;

        if( WeekModel.getCurrentMenuIsLunch()) {
            currentMenu = currentDay.getLunch();
            date.setText(currentMenu.getDate() + " midi");
        } else {
            currentMenu = currentDay.getDinner();
            date.setText(currentMenu.getDate() + " soir");
        }
        starterContent.setText("bonjour\nhhhikhi");
        mainCourseContent.setText(currentMenu.getMainCourse());
        System.out.println("AAAAAAA" + currentMenu.getMainCourse() + "BBBBB");
        System.out.println("bonjour");
        dessertContent.setText(currentMenu.getDessert());
    }

    public void onClickPreviousButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}