package com.HKTR.insalade;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.HKTR.insalade.Tools.isOnline;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventActivity extends BaseActivity {
    JSONArray eventsArray;
    Context context;
    SharedPreferences sharedPref;
    PullRefreshLayout refreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        changeTextViewFont((TextView) findViewById(R.id.headerEventTitle), fontPacifico);

        initiateScrollRefresh();

        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public void onClickPreviousButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String token = sharedPref.getString(getString(R.string.server_auth_token), "");
        if(token.length() == 0) {
            Intent intent = new Intent(this, EventInscriptionEmailActivity.class);
            startActivity(intent);
        }

        getEvents();
    }

    protected void getEvents() {
        // Fetch new event list if internet
        if (isOnline(context)) {
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
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("lastJSONEvent", response.toString());
                                    editor.commit();
                                    useJsonEventList();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("GETError : ", "Marche pas");
                                    useJsonEventList();
                                }
                            }
                    ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    String token = sharedPref.getString(getString(R.string.server_auth_token), "");
                    params.put("Authorization", token);

                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(jsObjRequest);
        }
        else //If no internet connexion
        {
            Toast.makeText(context, "Connexion internet non disponible", Toast.LENGTH_LONG).show();
            useJsonEventList();
        }
    }


    public void useJsonEventList(){
        // Get last Json from storage
        String defaultValue = "{\"status\":\"201\",\"events\":[{\"id_event\":-1,\"title\":\"Titre\",\"event_end\":\"Date de fin\",\"image_url\":\"random.jpg\",\"association\":\"Nom de l'association\",\"event_start\":\"Date de debut\",\"description\":\"Description\"}]}\"";
        String stringJSONEvent = sharedPref.getString("lastJSONEvent", defaultValue);

        // Display eventFragment if possible
        if(stringJSONEvent.equals(defaultValue)){
            Toast.makeText(context, "Pas d'évenement à afficher", Toast.LENGTH_LONG).show();
        }
        else
        {
            JSONObject response = null;
            try {
                response = new JSONObject(stringJSONEvent);
            }
            catch (JSONException e) {
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
        ArrayList usedImageFile = new ArrayList<String>();
        String[]  existingImageFiles = context.getDir("eventImageDir", Context.MODE_PRIVATE).list();
        ArrayList existingImageFilesList = new ArrayList(Arrays.asList(existingImageFiles));
        File filePath = context.getDir("eventImageDir", Context.MODE_PRIVATE);

        LinearLayout eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
        eventContainer.removeAllViews();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject event = jsonArray.getJSONObject(i);
                Log.e("event" + i + " : ", event.getString("title"));//TODO remove

                String imageUrl = event.getString("image_url");
                // Download image if not already on storage
                if(!existingImageFilesList.contains(imageUrl) && isOnline(context))
                {
                    new DownloadImageTask(event).execute("http://37.59.123.110/Web/web/uploads/documents/" + imageUrl);
                }
                else
                {
                    fragmentTransaction.add(R.id.eventContainer, createEventFragment(event.getString("title"), event.getString("description"), imageUrl, event.getString("event_start"), event.getString("event_end")));
                }

                usedImageFile.add(imageUrl);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        fragmentTransaction.commitAllowingStateLoss();



        // if existing file not in usedImageFile, then remove it form storage
        for(String s : existingImageFiles){
            if (!usedImageFile.contains(s)){
                File fileToRemove = new File(filePath.toString()+File.separatorChar+s);
                fileToRemove.delete();
            }
        }
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

    public void onClickAddDateToCalendar(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            RelativeLayout parentView = (RelativeLayout) view.getParent();
            String title = ((TextView) parentView.findViewById(R.id.eventTitle)).getText().toString();
            String description = ((TextView) parentView.findViewById(R.id.eventDescription)).getText().toString();

            String ligne, beginDate, beginTimeHour, endDate, endTimeHour;
            int beginDay = 0, beginMonth = 0, beginHour = 0, beginMinute = 0, endDay = 0, endMonth = 0, endHour = 0, endMinute = 0;

            try{
                Pattern p = Pattern .compile(".*(([0-9]{2})/([0-9]{2})).*(([0-9]{2}):([0-9]{2})).*(([0-9]{2})/([0-9]{2})).*(([0-9]{2}):([0-9]{2}))");
                String entree = ((TextView) view).getText().toString();
                Matcher m = p.matcher(entree);
                while (m.find()) {

                    ligne = m.group(0);
                    beginDate = m.group(1);
                    beginDay = Integer.decode(m.group(2));
                    beginMonth = Integer.decode(m.group(3));
                    beginTimeHour = m.group(4);
                    beginHour = Integer.decode(m.group(5));
                    beginMinute = Integer.decode(m.group(6));
                    endDate = m.group(7);
                    endDay = Integer.decode(m.group(8));
                    endMonth = Integer.decode(m.group(9));
                    endTimeHour = m.group(10);
                    endHour = Integer.decode(m.group(11));
                    endMinute = Integer.decode(m.group(12));
                }
            }catch(PatternSyntaxException ignored){
            }

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(beginTime.get(Calendar.YEAR), beginMonth, beginDay, beginHour, beginMinute);
            Calendar endTime = Calendar.getInstance();
            endTime.set(endTime.get(Calendar.YEAR), endMonth, endDay, endHour, endMinute);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, description);
            startActivity(intent);
        }
    }

    public void onClickHeaderText(View view) {
        ((ScrollView) findViewById(R.id.EventScrollView)).fullScroll(ScrollView.FOCUS_UP);
    }

    // Fetch image from url
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        String ImageName;
        JSONObject event;

        public DownloadImageTask(JSONObject JSONevent) {
            try {
                event = JSONevent;
                ImageName = event.getString("image_url");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            try {
                fragmentTransaction.add(R.id.eventContainer, createEventFragment(event.getString("title"), event.getString("description"), ImageName, event.getString("event_start"), event.getString("event_end")));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            fragmentTransaction.commitAllowingStateLoss();;
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String eventImageName){
        ContextWrapper cw = new ContextWrapper(context);
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

    private void initiateScrollRefresh() {
        PullRefreshLayout.OnRefreshListener onRefreshListener = new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isOnline(context)) {
                            // Delete imageFiles on internal Storage
                            String[]  existingImageFiles = context.getDir("eventImageDir", Context.MODE_PRIVATE).list();
                            File filePath = context.getDir("eventImageDir", Context.MODE_PRIVATE);
                            for(String s : existingImageFiles){
                                File fileToRemove = new File(filePath.toString()+File.separatorChar+s);
                                fileToRemove.delete();
                            }

                            // Delete saved Json
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove("lastJSONEvent");
                            editor.commit();


                            getEvents();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Connexion internet non disponible pour mettre à jour", Toast.LENGTH_LONG).show();
                        }

                        refreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        };

        refreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        // listen refresh event
        refreshLayout.setOnRefreshListener(onRefreshListener);
    }

}