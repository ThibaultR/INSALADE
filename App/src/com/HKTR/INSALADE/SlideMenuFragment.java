package com.HKTR.INSALADE;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;

/**
 * Created by Thibault on 25/10/2014.
 */
public class SlideMenuFragment extends Fragment {

    private static Integer i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.menu_slide_page, container, false);



        TextView date = (TextView) rootView.findViewById(R.id.dateText);
        TextView starterContent = (TextView) rootView.findViewById(R.id.starterContent);
        TextView mainCourseContent = (TextView) rootView.findViewById(R.id.mainCourseContent);
        TextView dessertContent = (TextView) rootView.findViewById(R.id.dessertContent);

        TextView starterTitle = (TextView) rootView.findViewById(R.id.starterTitle);
        TextView mainCourseTitle = (TextView) rootView.findViewById(R.id.mainCourseTitle);
        TextView dessertTitle = (TextView) rootView.findViewById(R.id.dessertTitle);

        //Change date font
        Typeface fontExistenceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Existence-Light.otf");
        Typeface nexaRust = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaRustSlab-BlackShadow01.otf");
        date.setTypeface(fontExistenceLight);

        //Change menu content font
        starterContent.setTypeface(fontExistenceLight);
        mainCourseContent.setTypeface(fontExistenceLight);
        dessertContent.setTypeface(fontExistenceLight);

        //Change menu title font
        starterTitle.setTypeface(nexaRust);
        mainCourseTitle.setTypeface(nexaRust);
        dessertTitle.setTypeface(nexaRust);



        //DayModel currentDay = WeekModel.getWeekList().get(42).getWeek().get(WeekModel.getCurrentMenuId());// TODO : to generalize
        DayModel currentDay = WeekModel.getDayById(i);// TODO : to generalize
        MenuModel currentMenu;

        if(i%2 == 0) {
            currentMenu = currentDay.getLunch();
            date.setText(currentMenu.getDate() + " midi");
        } else {
            currentMenu = currentDay.getDinner();
            date.setText(currentMenu.getDate() + " soir");
        }
        starterContent.setText(currentMenu.getStarter());
        mainCourseContent.setText(currentMenu.getMainCourse());
        dessertContent.setText(currentMenu.getDessert());

        i++;

        return rootView;
    }
}