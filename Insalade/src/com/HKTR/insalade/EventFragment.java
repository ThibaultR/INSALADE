package com.HKTR.insalade;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String Title = args.getString("Title", "DefaultTitle");
        String Description = args.getString("Description", "DefaultDescription");
        String ImageUrl = args.getString("ImageUrl", "DefaultImageUrl");//TODO defaultimage
        String Date = args.getString("Date", "Aujourd'hui maybe");

        // Inflate the layout for this fragment
        RelativeLayout eventFragment = (RelativeLayout) inflater.inflate(R.layout.event_fragment, container, false);

        TextView eventTitle = (TextView) eventFragment.findViewById(R.id.eventTitle);
        eventTitle.setText(Title);
        TextView eventDescription = (TextView) eventFragment.findViewById(R.id.eventDescription);
        eventDescription.setText(Description);
        TextView eventDate = (TextView) eventFragment.findViewById(R.id.eventDate);
        eventDate.setText(Date);
        ImageButton eventImage = (ImageButton) eventFragment.findViewById(R.id.eventImage);



        File filePath = getActivity().getDir("eventImageDir", Context.MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT < 16) {
            eventImage.setBackgroundDrawable(Drawable.createFromPath(filePath.toString()+File.separatorChar+ImageUrl));
        } else {
            eventImage.setBackground(Drawable.createFromPath(filePath.toString()+File.separatorChar+ImageUrl));
        }

        return eventFragment;
    }
}
