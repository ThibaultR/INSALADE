package com.HKTR.insalade;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hyukchan on 10/03/2015.
 */
public class EventInscriptionCodeActivity extends EventInscriptionActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        eventInscriptionSubTitle.setText(R.string.eventInscriptionCodeSubtitle);
        modal.setBackgroundResource(R.drawable.event_inscription_code_modal);
        submitButton.setImageResource(R.drawable.bouton_valider_code);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onSubmitEventCodeInscription(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        eventInscriptionInput.setHint("Code");
        eventInscriptionInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void onSubmitEventCodeInscription(View view) throws JSONException {
        final String password = eventInscriptionInput.getText().toString();

        String url = "http://37.59.123.110:443/sessions/";
        RequestQueue queue = Volley.newRequestQueue(this);

        String device_id = sharedPref.getString(getString(R.string.device_id), "");

        JSONObject params = new JSONObject();
        params.put("id_device", device_id);

        String email = sharedPref.getString(getString(R.string.saved_email), "");

        params.put("mail", email);
        params.put("password", password);
        params.put("os", "android");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    //save password and token
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString(getString(R.string.server_auth_token), response.getString("token"));
                                    editor.putString(getString(R.string.server_password), password);
                                    editor.putInt(getString(R.string.push_menu), 1);
                                    editor.putInt(getString(R.string.push_event), 1);
                                    editor.putInt(getString(R.string.push_other), 1);
                                    editor.commit();

                                    changeUserPushConfig(1,1);
                                    changeMenuPushConfig(1);

                                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        shakeModal(modal);
                    }
                });

        queue.add(jsObjRequest);
    }
}