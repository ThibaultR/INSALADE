package com.HKTR.insalade;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Hyukchan on 09/03/2015.
 */
public class BaseActivity extends Activity {

    /* FONTS */
    protected Typeface fontPacifico;
    protected Typeface fontLatoLight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        fontLatoLight = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
    }

    /*
     * change font of a TextView
     */
    public void changeTextViewFont(TextView textView, Typeface font) {
        textView.setTypeface(font);
    }

    /*
     * method called to go back to the precedent activity
     */
    public void onClickPreviousButton(View view) {
        onBackPressed();
    }
}