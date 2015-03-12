package com.HKTR.insalade;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class BaseActivity extends Activity {

    /* FONTS */
    public static Typeface fontPacifico;
    public static Typeface fontRobotoLight;
    public static Typeface fontRobotoRegular;

    public static SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        fontRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        fontRobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    /*
     * change font of a TextView
     */
    public static void changeTextViewFont(TextView textView, Typeface font) {
        textView.setTypeface(font);
    }

    /*
     * method called to go back to the precedent activity
     */
    public void onClickPreviousButton(View view) {
        onBackPressed();
    }

    /*
     * To update user's push config on server (on or off)
     */
    public void changeUserPushConfig(int event, int other) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.59.123.110:443/users/pushs/";

        JSONObject params = new JSONObject();

        String token = sharedPref.getString("registration_id", "");

        params.put("token", token);
        params.put("os", "android");
        params.put("push_event", event);
        params.put("push_other", other);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String auth_token = sharedPref.getString(getString(R.string.server_auth_token), "");
                params.put("Authorization", auth_token);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    /*
     * To update user's push config on server (on or off)
     */
    public void changeMenuPushConfig(int push_menu) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.59.123.110:443/pushs/";

        JSONObject params = new JSONObject();

        String token = sharedPref.getString("registration_id", "");
        String device_id = sharedPref.getString(getString(R.string.device_id), "");

        params.put("token", token);
        params.put("os", "android");
        params.put("push_menu", push_menu);
        params.put("id_device", device_id);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String auth_token = sharedPref.getString(getString(R.string.server_auth_token), "");
                params.put("Authorization", auth_token);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }
}