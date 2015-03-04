package com.HKTR.INSALADE;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.WeekModel;

import java.util.Calendar;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class SlideMenuActivity extends FragmentActivity {
    LinearLayout navigationButtons;
    /**
     * The number of pages (Number of week times seven days times two menus by day).
     */
    private static int numPages = WeekModel.getWeekList().size() * 7 * 2; //TODO : care to the number of menun

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private int oldMenuIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_slide);

        //Change header menu title font
        Typeface fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        TextView headerMenuTitle = (TextView) findViewById(R.id.headerMenuTitle);
        headerMenuTitle.setTypeface(fontPacifico);

        navigationButtons = (LinearLayout) findViewById(R.id.navigationButtons);

        //Fill navigationButtons
        String[] dayNameList = getResources().getStringArray(R.array.dayNameList);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, XmlFileGetter.weekNumber);

        for(int i = 0; i<7; i++){
            RelativeLayout navigationButton = (RelativeLayout) getLayoutInflater().inflate(R.layout.day_button_fragment, navigationButtons, false);

            // Set Day name (Lun, Mar...)
            TextView dayName = (TextView) navigationButton.findViewById(R.id.dayName);
            dayName.setText(dayNameList[i]);

            // Set Day number
            TextView dayNumber = (TextView) navigationButton.findViewById(R.id.dayNumber);
            cal.set(Calendar.DAY_OF_WEEK, (i+2)%7);
            dayNumber.setText(""+cal.get(Calendar.DAY_OF_MONTH));

            // Set tickmarks color if closed
            DayModel currentDay =  WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(i);
            if (currentDay.getLunch().isClosed()){
                navigationButton.findViewById(R.id.tickMarkLunch).setBackgroundResource(R.drawable.closed_tickmark);
            }

            if (currentDay.getDinner().isClosed()){
                navigationButton.findViewById(R.id.tickMarkDinner).setBackgroundResource(R.drawable.closed_tickmark);
            }


            navigationButtons.addView(navigationButton);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        numPages = WeekModel.getWeekList().size() * 7 * 2;
        int indexPage = getIntent().getIntExtra("idPage",0);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(indexPage);

        updateCurrentMenuIndicationColor(indexPage, false);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateCurrentMenuIndicationColor(i, true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });
    }

    // TODO Factoriser
    public void updateCurrentMenuIndicationColor(int pageIndex, boolean updateOldDayButton) {
        if(updateOldDayButton) {
            RelativeLayout oldNavigationButton = (RelativeLayout) navigationButtons.getChildAt(oldMenuIndex / 2);
            if (oldMenuIndex % 2 == 0) {
                View tickMarkLunch = oldNavigationButton.findViewById(R.id.tickMarkLunch);
                if (WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(oldMenuIndex / 2).getLunch().isClosed()) {
                    tickMarkLunch.setBackgroundResource(R.drawable.closed_tickmark);
                } else {
                    tickMarkLunch.setBackgroundResource(R.drawable.default_tickmark);
                }
            } else {
                View tickMarkDinner = oldNavigationButton.findViewById(R.id.tickMarkDinner);
                if (WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(oldMenuIndex / 2).getDinner().isClosed()) {
                    tickMarkDinner.setBackgroundResource(R.drawable.closed_tickmark);
                } else {
                    tickMarkDinner.setBackgroundResource(R.drawable.default_tickmark);
                }
            }

            ((TextView) oldNavigationButton.findViewById(R.id.dayNumber)).setTextColor(getResources().getColor(R.color.mainTextColor));
            ((TextView) oldNavigationButton.findViewById(R.id.dayName)).setTextColor(getResources().getColor(R.color.mainTextColor));
        }


        RelativeLayout navigationButton = (RelativeLayout) navigationButtons.getChildAt(pageIndex/2);
        if(pageIndex%2 == 0) {
            navigationButton.findViewById(R.id.tickMarkLunch).setBackgroundResource(R.drawable.selected_tickmark);
        } else {
            navigationButton.findViewById(R.id.tickMarkDinner).setBackgroundResource(R.drawable.selected_tickmark);
        }
        ((TextView) navigationButton.findViewById(R.id.dayNumber)).setTextColor(getResources().getColor(R.color.mainColor));
        ((TextView) navigationButton.findViewById(R.id.dayName)).setTextColor(getResources().getColor(R.color.mainColor));

        oldMenuIndex = pageIndex;
    }


    //TODO : remove
    public void onClickPreviousMenu(View view) {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    //TODO : remove
    public void onClickNextMenu(View view) {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

    public void onClickNavigationButton(View view) {
        int i;
        for(i = 0; i < navigationButtons.getChildCount(); i++){
            RelativeLayout navigationButton = (RelativeLayout) navigationButtons.getChildAt(i);
            if(view.equals(navigationButton)){
                break;
            }
        }

        if(mPager.getCurrentItem() == i*2){
            mPager.setCurrentItem(i*2 +1, false);
        }else {
            mPager.setCurrentItem(i*2, false);
        }
    }

    public void onClickHeaderText(View view) {
        //Sunday = 1, Monday = 2 ...
        Calendar now = Calendar.getInstance();
        int today = (now.get(Calendar.DAY_OF_WEEK) - 2)%7;
        int timeH = now.get(Calendar.HOUR_OF_DAY);

        int menuNumber = 0;
        //afficher le déjeuner
        if(timeH < 14) {
            menuNumber = today * 2;
        }
        //afficher le dinner
        else {
            menuNumber = today * 2 + 1;
        }

        mPager.setCurrentItem(menuNumber, false);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SlideMenuFragment.create(position);
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }


    public void onClickPreviousButton(View view) {
        onBackPressed();
    }
}