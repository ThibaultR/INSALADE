package com.HKTR.insalade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

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

            }
        });
        eventInscriptionInput.setHint("Code");
        eventInscriptionInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void onSubmitEventInscription(View view) throws JSONException {
        final String email = eventInscriptionInput.getText().toString();

        String url = "http://37.59.123.110:443/users/";
        RequestQueue queue = Volley.newRequestQueue(this);

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        JSONObject params = new JSONObject();
        params.put("id_device", android_id);
        params.put("mail", email);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

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