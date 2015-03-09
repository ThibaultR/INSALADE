package com.HKTR.insalade;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import android.provider.Settings.Secure;

/**
 * Created by Hyukchan on 08/03/2015.
 */
public class EventInscriptionActivity extends Activity {

    RelativeLayout modal;
    ImageButton submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_inscription_activity);
        changeHeaderFont();
        modal = (RelativeLayout) findViewById(R.id.eventInscriptionModal);
        changeTitlesFont();
        handleOnSubmitEmailInput();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /*
     * When you validate the EditText, nothing happens. Thus, we need this method to activate a submit action
     */
    private void handleOnSubmitEmailInput() {
        EditText emailEditText = (EditText) findViewById(R.id.eventInscriptionEmailInput);
        submitButton = (ImageButton) findViewById(R.id.button_send);
        emailEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    private void changeTitlesFont() {
        Typeface fontLatoLight = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
        TextView eventInscriptionTitle = (TextView) findViewById(R.id.eventInscriptionTitle);
        TextView eventInscriptionSubTitle = (TextView) findViewById(R.id.eventInscriptionSubTitle);
        eventInscriptionTitle.setTypeface(fontLatoLight);
        eventInscriptionSubTitle.setTypeface(fontLatoLight);
    }

    public void onSubmitEventInscription(View view) throws JSONException {
        EditText eventInscriptionEmailInput = (EditText) findViewById(R.id.eventInscriptionEmailInput);
        final String email = eventInscriptionEmailInput.getText().toString();

        String url = "http://37.59.123.110:443/users/";
        RequestQueue queue = Volley.newRequestQueue(this);

        String android_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);

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

    private void shakeModal(RelativeLayout modal) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        modal.startAnimation(shakeAnimation);
        Log.e("haha", "shake babe");
    }

    private void changeHeaderFont() {
        Typeface fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        TextView headerMenuTitle = (TextView) findViewById(R.id.insalade_logo);
        headerMenuTitle.setTypeface(fontPacifico);
    }
}