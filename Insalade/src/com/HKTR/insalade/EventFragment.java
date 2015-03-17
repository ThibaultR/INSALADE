package com.HKTR.insalade;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventFragment extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String Title = args.getString("Title");
        String Description = args.getString("Description");
        String ImageUrl = args.getString("ImageUrl");
        String Date = args.getString("Date");//Default values dropped to support older android version

        // Inflate the layout for this fragment
        RelativeLayout eventFragment = (RelativeLayout) inflater.inflate(R.layout.event_fragment, container, false);

        TextView eventTitle = (TextView) eventFragment.findViewById(R.id.eventTitle);
        eventTitle.setText(Title);
        TextView eventDescription = (TextView) eventFragment.findViewById(R.id.eventDescription);
        eventDescription.setText(Description);
        TextView eventDate = (TextView) eventFragment.findViewById(R.id.eventDate);
        eventDate.setText(Date);
        SquareImageView eventImage = (SquareImageView) eventFragment.findViewById(R.id.eventImage);

        BaseActivity.changeTextViewFont(eventTitle, BaseActivity.fontRobotoRegular);
        BaseActivity.changeTextViewFont(eventDescription, BaseActivity.fontRobotoLight);
        BaseActivity.changeTextViewFont(eventDate, BaseActivity.fontRobotoLight);

        File filePath = getActivity().getDir("eventImageDir", Context.MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT < 16) {
            eventImage.setBackgroundDrawable(Drawable.createFromPath(filePath.toString()+File.separatorChar+ImageUrl));
        } else {
            eventImage.setBackground(Drawable.createFromPath(filePath.toString()+File.separatorChar+ImageUrl));
        }

        return eventFragment;
    }
}
