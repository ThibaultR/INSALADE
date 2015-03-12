package com.HKTR.insalade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Hyukchan on 09/03/2015.
 */
public class EventInscriptionEmailActivity extends EventInscriptionActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClickPreviousButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}