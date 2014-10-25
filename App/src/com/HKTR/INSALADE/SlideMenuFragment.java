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
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static SlideMenuFragment create(int pageNumber) {
        SlideMenuFragment fragment = new SlideMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SlideMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }



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
        DayModel currentDay = WeekModel.getDayById(getPageNumber());// TODO : to generalize
        MenuModel currentMenu;

        if(getPageNumber()%2 == 0) {
            currentMenu = currentDay.getLunch();
            date.setText(currentMenu.getDate() + " midi");
        } else {
            currentMenu = currentDay.getDinner();
            date.setText(currentMenu.getDate() + " soir");
        }
        starterContent.setText(currentMenu.getStarter());
        mainCourseContent.setText(currentMenu.getMainCourse());
        dessertContent.setText(currentMenu.getDessert());

        return rootView;
    }


    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}