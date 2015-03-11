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
    public static Typeface fontPacifico;
    public static Typeface fontRobotoLight;
    public static Typeface fontRobotoRegular;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        fontRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        fontRobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
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
}