package com.HKTR.insalade;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.HKTR.insalade.model.DayModel;
import com.HKTR.insalade.model.MenuModel;
import com.HKTR.insalade.model.WeekModel;

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

        TextView menuPageDate = (TextView) rootView.findViewById(R.id.menuPageDate);
        Typeface fontLatoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
        //Change date font
        menuPageDate.setTypeface(fontLatoLight);

        //get currentMenu
        DayModel currentDay = WeekModel.getDayById(getPageNumber());
        MenuModel currentMenu;

        if(getPageNumber()%2 == 0) {
            currentMenu = currentDay.getLunch();
            menuPageDate.setText(currentMenu.getDate() + " midi");
        } else {
            currentMenu = currentDay.getDinner();
            menuPageDate.setText(currentMenu.getDate() + " soir");
        }

        Typeface fontLatoHeavy = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Heavy.ttf");

        TextView starterTitle = (TextView) rootView.findViewById(R.id.starterTitle);
        TextView mainCourseTitle = (TextView) rootView.findViewById(R.id.mainCourseTitle);
        TextView dessertTitle = (TextView) rootView.findViewById(R.id.dessertTitle);

        starterTitle.setTypeface(fontLatoHeavy);
        mainCourseTitle.setTypeface(fontLatoHeavy);
        dessertTitle.setTypeface(fontLatoHeavy);

        TextView starterContent = (TextView) rootView.findViewById(R.id.starterContent);
        TextView mainCourseContent = (TextView) rootView.findViewById(R.id.mainCourseContent);
        TextView dessertContent = (TextView) rootView.findViewById(R.id.dessertContent);

        starterContent.setText(currentMenu.getStarter());
        mainCourseContent.setText(currentMenu.getMainCourse());
        dessertContent.setText(currentMenu.getDessert());

        starterContent.setTypeface(fontLatoLight);
        mainCourseContent.setTypeface(fontLatoLight);
        dessertContent.setTypeface(fontLatoLight);

        handleNestedScrollProblem(rootView);

        return rootView;
    }

    private void handleNestedScrollProblem(ViewGroup rootView) {
        RelativeLayout menuScrollView = (RelativeLayout) rootView.findViewById(R.id.MenuScrollView);
        menuScrollView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                com.baoyz.widget.PullRefreshLayout pullRefreshLayout = (com.baoyz.widget.PullRefreshLayout) v.getParent().getParent().getParent().getParent().getParent().getParent();


                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (((ScrollView) v.getParent()).getScrollY() > 0) {
                        pullRefreshLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (((ScrollView) v.getParent()).getScrollY() == 0) {
                        pullRefreshLayout.requestDisallowInterceptTouchEvent(false);
                    }
                }

                return true;
            }
        });
    }


    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}