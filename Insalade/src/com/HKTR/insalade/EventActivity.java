package com.HKTR.insalade;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;

import static com.HKTR.insalade.Tools.isOnline;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventActivity extends Activity {
    JSONArray eventsArray;

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

        //TODO if not internet acces look on memory
        //Fetch event list and display them
        if (isOnline(getApplicationContext())) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://37.59.123.110:443/events/";

            // Request a string response from the provided URL.
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,
                     url,
                     null,
                     new Response.Listener<JSONObject>() {
                         @Override
                         public void onResponse(JSONObject response) {
                             Log.e("GET : ", response.toString());//TODO remove

                             // Save last Json for offline purpose
                             Context context = getApplicationContext();
                             SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                             SharedPreferences.Editor editor = sharedPref.edit();
                             editor.putString("lastJSONEvent", response.toString());
                             editor.commit();

                             eventsArray = response.optJSONArray("events"); //TODO save last json for offline purpose
                             if (eventsArray == null) {
                                 Log.e("Array : ", "Pas d'event");
                             }
                             else {
                                 displayEventFragment(eventsArray);
                             }
                         }
                     },
                     new Response.ErrorListener() {
                         @Override
                         public void onErrorResponse(VolleyError error) {
                             Log.e("GETError : ", "Marche pas");
                         }
                     }
                    ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "6f79dedc-5726-4510-938f-81206fbcb2e8");

                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(jsObjRequest);
        }
        else //If no internet connexion
        {
            Toast.makeText(getApplicationContext(), "Connexion internet non disponible", Toast.LENGTH_LONG).show();

            Context context = getApplicationContext();
            SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String defaultValue = "{\"status\":\"201\",\"events\":[{\"id_event\":-1,\"title\":\"Titre\",\"event_end\":\"Date de fin\",\"image_url\":\"random.jpg\",\"association\":\"Nom de l'association\",\"event_start\":\"Date de debut\",\"description\":\"Description\"}]}\"";
            String stringJSONEvent = sharedPref.getString("lastJSONEvent", defaultValue);

            if(stringJSONEvent.equals(defaultValue)){
                Toast.makeText(getApplicationContext(), "Pas de données à afficher", Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONObject response = null;
                try {
                    response = new JSONObject(stringJSONEvent);
                }
                catch (JSONException e) {
                    Log.e("Get saved JSONEvent : ", "Problem");
                    e.printStackTrace();
                }

                eventsArray = response.optJSONArray("events");
                if (eventsArray == null) {
                    Log.e("Array : ", "Pas d'event");
                }
                else {
                    displayEventFragment(eventsArray);
                }
            }
        }
    }

    public EventFragment createEventFragment(String eventTitle, String eventDescription, String eventImageUrl, String eventStartTime, String eventEndTime) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString("Title", eventTitle);
        args.putString("Description", eventDescription);
        args.putString("ImageUrl", eventImageUrl);
        String date = "Du " + eventStartTime + " au " + eventEndTime;
        args.putString("Date", date);

        fragment.setArguments(args);
        return fragment;
    }

    public void displayEventFragment(JSONArray jsonArray) {
        LinearLayout eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
        eventContainer.removeAllViews();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject event = jsonArray.getJSONObject(i);
                Log.e("event" + i + " : ", event.getString("title"));//TODO remove
                String imageUrl = event.getString("image_url");
                fragmentTransaction.add(R.id.eventContainer, createEventFragment(event.getString("title"), event.getString("description"), imageUrl, event.getString("event_start"), event.getString("event_end")));

                // Download image if not already on storage
                String[]  existingImageFiles = getApplicationContext().getDir("eventImageDir", Context.MODE_PRIVATE).list();
                ArrayList existingImageFilesList = new ArrayList(Arrays.asList(existingImageFiles));

                if(!existingImageFilesList.contains(imageUrl))
                {
                    Log.e("DL image : ", "start");
                    new DownloadImageTask(imageUrl).execute("http://37.59.123.110/Web/web/uploads/documents/" + imageUrl);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        fragmentTransaction.commit();
    }

    public void onClickPreviousButton(View view) {
        onBackPressed();
    }

    public void onClickEventImage(View view) {
        FrameLayout imageGroup = (FrameLayout) view.getParent();
        TextView eventDescription = (TextView) imageGroup.findViewById(R.id.eventDescription);

        if (eventDescription.getVisibility() == View.VISIBLE) {
            eventDescription.setVisibility(View.GONE);
        }
        else {
            eventDescription.setVisibility(View.VISIBLE);
        }
    }

    // Fetch image from url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        String ImageName;

        public DownloadImageTask(String eventImageName) { ImageName = eventImageName;}

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmapImage = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmapImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmapImage;
        }

        protected void onPostExecute(Bitmap result) {
            saveToInternalStorage(result, ImageName);
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String eventImageName){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/eventImageDir
        File directory = cw.getDir("eventImageDir", Context.MODE_PRIVATE);
        // Create eventImageDir
        File mypath = new File(directory,eventImageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
}