package com.HKTR.insalade;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Date;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinearLayout eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
        eventContainer.removeAllViews();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //TODO foreach event
        fragmentTransaction.add(R.id.eventContainer, createEventFragment("Title de l'event", "description trop bien de l'event","tasoeur.png", null, null));//TODO date
        fragmentTransaction.commit();
    }


    public EventFragment createEventFragment(String eventTitle, String eventDescription, String eventImageUrl, Date eventStartTime, Date eventEndTime){
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString("Title", eventTitle);
        args.putString("Description", eventDescription);
        args.putString("ImageUrl", eventImageUrl);
        //TODO date + image ?
        fragment.setArguments(args);
        return fragment;
    }

    public void onClickPreviousButton(View view) {onBackPressed();}

    public void onClickEventImage(View view) {
        FrameLayout imageGroup = (FrameLayout) view.getParent();
        TextView eventDescription = (TextView) imageGroup.findViewById(R.id.eventDescription);

        if(eventDescription.getVisibility() == View.VISIBLE) {
            eventDescription.setVisibility(View.GONE);
        } else {
            eventDescription.setVisibility(View.VISIBLE);
        }
    }
}