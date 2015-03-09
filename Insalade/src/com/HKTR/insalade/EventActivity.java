package com.HKTR.insalade;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://37.59.123.110:443/events/";

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,
                 url,
                 null,
                 new Response.Listener<JSONObject>() {
                     @Override
                     public void onResponse(JSONObject response) {
                         Log.e("GET : ", response.toString());
                         eventsArray = response.optJSONArray("events"); //TODO save last json for offline purpose
                         if(eventsArray == null){
                             Log.e("Array : ", "Pas d'event");
                         } else {
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
                )
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "a37541a6-4b80-487d-88c2-c0b4bce927bf");

                        return params;
                    }
                };

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }


    public EventFragment createEventFragment(String eventTitle, String eventDescription, String eventImageUrl, String eventStartTime, String eventEndTime){
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

    public void displayEventFragment(JSONArray jsonArray){
        LinearLayout eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
        eventContainer.removeAllViews();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject event = jsonArray.getJSONObject(i);
                Log.e("event"+i+" : ", event.getString("title"));//TODO remove
                fragmentTransaction.add(R.id.eventContainer, createEventFragment(event.getString("title"), event.getString("description"),event.getString("image_url"), event.getString("event_start"), event.getString("event_end")));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        fragmentTransaction.commit();
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