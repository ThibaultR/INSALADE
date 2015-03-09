package com.HKTR.insalade;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;

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

        new DownloadImageTask((ImageButton) eventFragment.findViewById(R.id.eventImage)).execute("http://37.59.123.110/Web/web/uploads/documents/"+ImageUrl); //TODO save Image for offline purpose

        return eventFragment;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageButton bmImage;

        public DownloadImageTask(ImageButton bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
