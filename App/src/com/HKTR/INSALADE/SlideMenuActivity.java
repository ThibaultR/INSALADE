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
import android.widget.TextView;
import com.HKTR.INSALADE.model.WeekModel;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class SlideMenuActivity extends FragmentActivity {
    /**
     * The number of pages (Number of week times seven days times two menus by day).
     */
    private static final int NUM_PAGES = WeekModel.getWeekList().size() * 7 * 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_slide);

        int indexPage = getIntent().getIntExtra("idPage",0);

        //Change header menu title font
        Typeface fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        TextView headerMenuTitle = (TextView) findViewById(R.id.headerMenuTitle);
        headerMenuTitle.setTypeface(fontPacifico);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(indexPage);
    }



    public void onClickPreviousMenu(View view) {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    public void onClickNextMenu(View view) {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
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
            return NUM_PAGES;
        }
    }


    public void onClickPreviousButton(View view) {
        onBackPressed();
    }
}