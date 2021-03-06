package com.HKTR.INSALADE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import static com.HKTR.INSALADE.Tools.isOnline;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class EventInscriptionActivity extends BaseActivity {

    Context context;

    RelativeLayout modal;
    Button submitButton;

    TextView eventInscriptionTitle;
    TextView eventInscriptionSubTitle;
    EditText eventInscriptionInput;

    boolean inputFocus = false;

    int eventInscriptionTitleHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_inscription_activity);

        context = getApplicationContext();
        changeTextViewFont((TextView) findViewById(R.id.insalade_logo), fontPacifico);

        eventInscriptionInput = (EditText) findViewById(R.id.eventInscriptionInput);

        eventInscriptionTitle = (TextView) findViewById(R.id.eventInscriptionTitle);
        eventInscriptionSubTitle = (TextView) findViewById(R.id.eventInscriptionSubTitle);

        changeTextViewFont(eventInscriptionTitle, fontRobotoLight);
        changeTextViewFont(eventInscriptionSubTitle, fontRobotoLight);

        modal = (RelativeLayout) findViewById(R.id.eventInscriptionModal);
        submitButton = (Button) findViewById(R.id.button_send);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        handleOnFocusInput();
        handleOnSubmitInput();

        eventInscriptionTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                eventInscriptionTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                eventInscriptionTitleHeight = eventInscriptionTitle.getHeight();
            }
        });
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
                /* inputFocus : so the animation is done only once when we focus on the input */
                if (hasFocus && !inputFocus) {
                    inputFocus = true;

                    TranslateAnimation onInputFocusTitleAnimation = new TranslateAnimation(0,-eventInscriptionTitle.getWidth(),0,0);
                    onInputFocusTitleAnimation.setDuration(300);
                    onInputFocusTitleAnimation.setFillAfter(true);
                    onInputFocusTitleAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            eventInscriptionTitle.setVisibility(View.GONE);
                            eventInscriptionTitle.clearAnimation();
                            eventInscriptionSubTitle.clearAnimation();
                            modal.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    eventInscriptionTitle.startAnimation(onInputFocusTitleAnimation);

                    int animationDelta = Tools.dpToPx(eventInscriptionTitleHeight);

                    TranslateAnimation onInputFocusModalAnimation = new TranslateAnimation(0,0,0,-animationDelta);
                    onInputFocusModalAnimation.setDuration(300);
                    onInputFocusModalAnimation.setFillEnabled(false);
                    onInputFocusModalAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            eventInscriptionSubTitle.clearAnimation();
                            eventInscriptionTitle.clearAnimation();
                            modal.clearAnimation();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    modal.startAnimation(onInputFocusModalAnimation);
                    eventInscriptionSubTitle.startAnimation(onInputFocusModalAnimation);
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
        if(isOnline(context)) {
            final RelativeLayout loadingView = (RelativeLayout) findViewById(R.id.loadingPanel);
            loadingView.setVisibility(View.VISIBLE);

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
                                    loadingView.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(), EventInscriptionCodeActivity.class);
                                    startActivity(intent);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            shakeModal(modal);
                            submitButton.setClickable(true);
                            loadingView.setVisibility(View.GONE);
                        }
                    });

            jsObjRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsObjRequest);
        }
        else {
            Toast.makeText(context, "Connexion internet non disponible", Toast.LENGTH_LONG).show();
        }
    }
}