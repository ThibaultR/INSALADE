package com.HKTR.insalade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventInscriptionActivity extends BaseActivity {

    RelativeLayout modal;
    ImageButton submitButton;

    TextView eventInscriptionTitle;
    TextView eventInscriptionSubTitle;
    EditText eventInscriptionInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_inscription_activity);
        changeTextViewFont((TextView) findViewById(R.id.insalade_logo), fontPacifico);

        eventInscriptionInput = (EditText) findViewById(R.id.eventInscriptionInput);

        eventInscriptionTitle = (TextView) findViewById(R.id.eventInscriptionTitle);
        eventInscriptionSubTitle = (TextView) findViewById(R.id.eventInscriptionSubTitle);

        changeTextViewFont(eventInscriptionTitle, fontRobotoLight);
        changeTextViewFont(eventInscriptionSubTitle, fontRobotoLight);

        modal = (RelativeLayout) findViewById(R.id.eventInscriptionModal);
        submitButton = (ImageButton) findViewById(R.id.button_send);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        handleOnFocusInput();
        handleOnSubmitInput();
    }

    @Override
    protected void onResume() {
        super.onResume();

        submitButton.setClickable(true);
    }


    protected void shakeModal(RelativeLayout modal) {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        modal.startAnimation(shakeAnimation);
    }

    /*
     * Method used to hide the title and to translate to the top the modal to make some space when the keyboard shows up
     */
    protected void handleOnFocusInput() {
        eventInscriptionInput.setSelected(false);
        modal.requestFocus();
        eventInscriptionInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TranslateAnimation animate = new TranslateAnimation(0,-eventInscriptionTitle.getWidth(),0,0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    eventInscriptionTitle.startAnimation(animate);
                    eventInscriptionTitle.setVisibility(View.GONE);
                } else {

                    TranslateAnimation animate = new TranslateAnimation(-eventInscriptionTitle.getWidth(),0,0,0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    eventInscriptionTitle.startAnimation(animate);
                    eventInscriptionTitle.setVisibility(View.VISIBLE);

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(eventInscriptionInput.getWindowToken(), 0);
                }
            }
        });
    }

    /*
     * When you validate the EditText, nothing happens. Thus, we need this method to activate a submit action
     */
    private void handleOnSubmitInput() {
        EditText emailEditText = (EditText) findViewById(R.id.eventInscriptionInput);

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

    public void onSubmitEventInscriptionEmail(View view) throws JSONException {
        submitButton.setClickable(false);
        final String email = eventInscriptionInput.getText().toString();

        String url = "http://37.59.123.110:443/users/";
        RequestQueue queue = Volley.newRequestQueue(this);

        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.device_id), android_id);
        editor.commit();

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
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.saved_email), email);
                                editor.commit();

                                Intent intent = new Intent(getApplicationContext(), EventInscriptionCodeActivity.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        shakeModal(modal);
                        submitButton.setClickable(true);
                    }
                });

        queue.add(jsObjRequest);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

            }
        };
        handler.postDelayed(r, 5000);
    }
}