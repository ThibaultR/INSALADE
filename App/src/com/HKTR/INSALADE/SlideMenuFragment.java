package com.HKTR.INSALADE;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
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
        Typeface fontExistenceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Existence-Light.otf");

        TextView date = (TextView) rootView.findViewById(R.id.dateText);
        //Change date font
        date.setTypeface(fontExistenceLight);

        //get currentMenu
        DayModel currentDay = WeekModel.getDayById(getPageNumber());
        MenuModel currentMenu;

        if(getPageNumber()%2 == 0) {
            currentMenu = currentDay.getLunch();
            date.setText(currentMenu.getDate() + " midi");
        } else {
            currentMenu = currentDay.getDinner();
            date.setText(currentMenu.getDate() + " soir");
        }

        //If there is no menu then hide starter, mainCourse and dessert and show "closed" message
        if(currentMenu.isClosed()) {
            RelativeLayout starter = (RelativeLayout) rootView.findViewById(R.id.starter);
            RelativeLayout mainCourse = (RelativeLayout) rootView.findViewById(R.id.mainCourse);
            RelativeLayout dessert = (RelativeLayout) rootView.findViewById(R.id.dessert);

            starter.setVisibility(View.GONE);
            mainCourse.setVisibility(View.GONE);
            dessert.setVisibility(View.GONE);

            RelativeLayout menuContent = (RelativeLayout) rootView.findViewById(R.id.menuContent);

            //We create the ImageView containing the "closed" message
            ImageView closedMenu = (ImageView) inflater.inflate(R.layout.closedmenu_template, menuContent, false);

            menuContent.addView(closedMenu);
        }



        TextView starterContent = (TextView) rootView.findViewById(R.id.starterContent);
        TextView mainCourseContent = (TextView) rootView.findViewById(R.id.mainCourseContent);
        TextView dessertContent = (TextView) rootView.findViewById(R.id.dessertContent);

        TextView starterTitle = (TextView) rootView.findViewById(R.id.starterTitle);
        TextView mainCourseTitle = (TextView) rootView.findViewById(R.id.mainCourseTitle);
        TextView dessertTitle = (TextView) rootView.findViewById(R.id.dessertTitle);

        //Change menu content font
        starterContent.setTypeface(fontExistenceLight);
        mainCourseContent.setTypeface(fontExistenceLight);
        dessertContent.setTypeface(fontExistenceLight);

        //Change menu title font
        Typeface nexaRust = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaRustSlab-BlackShadow01.otf");
        starterTitle.setTypeface(nexaRust);
        mainCourseTitle.setTypeface(nexaRust);
        dessertTitle.setTypeface(nexaRust);

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