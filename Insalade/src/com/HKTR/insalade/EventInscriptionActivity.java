package com.HKTR.insalade;

import android.content.Context;
import android.os.Bundle;
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

/**
 * Created by Hyukchan on 08/03/2015.
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
}