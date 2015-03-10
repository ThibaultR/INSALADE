package com.HKTR.insalade;

import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hyukchan on 09/03/2015.
 */
public class EventInscriptionEmailActivity extends EventInscriptionActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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