package com.HKTR.INSALADE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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

        //Change header menu title font
        Typeface fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        TextView headerMenuTitle = (TextView) findViewById(R.id.headerMenuTitle);
        headerMenuTitle.setTypeface(fontPacifico);

        TextView date = (TextView) findViewById(R.id.dateText);
        TextView starterContent = (TextView) findViewById(R.id.starterContent);
        TextView mainCourseContent = (TextView) findViewById(R.id.mainCourseContent);
        TextView dessertContent = (TextView) findViewById(R.id.dessertContent);

        TextView starterTitle = (TextView) findViewById(R.id.starterTitle);
        TextView mainCourseTitle = (TextView) findViewById(R.id.mainCourseTitle);
        TextView dessertTitle = (TextView) findViewById(R.id.dessertTitle);

        //Change date font
        Typeface fontExistenceLight = Typeface.createFromAsset(getAssets(), "fonts/Existence-Light.otf");
        Typeface nexaRust = Typeface.createFromAsset(getAssets(), "fonts/NexaRustSlab-BlackShadow01.otf");
        date.setTypeface(fontExistenceLight);

        //Change menu content font
        starterContent.setTypeface(fontExistenceLight);
        mainCourseContent.setTypeface(fontExistenceLight);
        dessertContent.setTypeface(fontExistenceLight);

        //Change menu title font
        starterTitle.setTypeface(nexaRust);
        mainCourseTitle.setTypeface(nexaRust);
        dessertTitle.setTypeface(nexaRust);



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