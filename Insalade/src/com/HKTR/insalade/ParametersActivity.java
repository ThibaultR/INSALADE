package com.HKTR.insalade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class ParametersActivity extends BaseActivity {
    ToggleButton notificationMenuInput;
    ToggleButton notificationEventInput;
    ToggleButton notificationOtherInput;

    ImageButton deleteAccountButton;

    RelativeLayout loadingPanel;

    int push_event;
    int push_menu;
    int push_other;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameters_activity);
        changeTextViewFont((TextView) findViewById(R.id.layoutTitle), fontPacifico);

        changeTextViewFont((TextView) findViewById(R.id.notificationMenuLabel), fontRobotoLight);
        changeTextViewFont((TextView) findViewById(R.id.notificationEventLabel), fontRobotoLight);
        changeTextViewFont((TextView) findViewById(R.id.notificationOtherLabel), fontRobotoLight);

        notificationMenuInput = (ToggleButton) findViewById(R.id.notificationMenuInput);
        notificationEventInput = (ToggleButton) findViewById(R.id.notificationEventInput);
        notificationOtherInput = (ToggleButton) findViewById(R.id.notificationOtherInput);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        deleteAccountButton = (ImageButton) findViewById(R.id.button_deleteAccount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        push_event = sharedPref.getInt(getString(R.string.push_event), 0);
        push_menu = sharedPref.getInt(getString(R.string.push_menu), 0);
        push_other = sharedPref.getInt(getString(R.string.push_other), 0);

        String token = sharedPref.getString(getString(R.string.server_auth_token), "");

        if(push_event == 1) {
            notificationEventInput.setChecked(true);
        }

        if(push_menu == 1) {
            notificationMenuInput.setChecked(true);
        }

        if(push_other == 1) {
            notificationOtherInput.setChecked(true);
        }

        if (token.length() == 0) {
            deleteAccountButton.setVisibility(View.GONE);
            notificationEventInput.setEnabled(false);
            notificationOtherInput.setEnabled(false);
            notificationEventInput.setChecked(false);
            notificationOtherInput.setChecked(false);
        }
    }

    public void onToggleNotificationMenuClicked(View view) {
        disableParametersPanel();

        boolean on = ((ToggleButton) view).isChecked();

        int notificationMenu;

        if (on) {
            notificationMenu = 1;
        } else {
            notificationMenu = 0;
        }

        try {
            changeMenuPushConfig(notificationMenu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onToggleNotificationUserClicked(View view) {
        disableParametersPanel();

        boolean onEvent = ((ToggleButton) findViewById(R.id.notificationEventInput)).isChecked();
        boolean onOther = ((ToggleButton) findViewById(R.id.notificationOtherInput)).isChecked();

        int notificationEvent;

        int notificationOther;

        if (onEvent) {
            notificationEvent = 1;
        } else {
            notificationEvent = 0;
        }

        if (onOther) {
            notificationOther = 1;
        } else {
            notificationOther = 0;
        }

        try {
            changeUserPushConfig(notificationEvent, notificationOther);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disableParametersPanel() {
        loadingPanel.setVisibility(View.VISIBLE);

        notificationMenuInput.setClickable(false);
        notificationEventInput.setClickable(false);
        notificationOtherInput.setClickable(false);
    }

    public void enableParametersPanel() {
        loadingPanel.setVisibility(View.GONE);

        notificationMenuInput.setClickable(true);
        notificationEventInput.setClickable(true);
        notificationOtherInput.setClickable(true);
    }

    @Override
    public void onClickPreviousButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void changeMenuPushConfig(int push_menu) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.59.123.110:443/pushs/";

        JSONObject params = new JSONObject();

        String token = sharedPref.getString("registration_id", "");
        String device_id = sharedPref.getString(getString(R.string.device_id), "");

        params.put("token", token);
        params.put("os", "android");
        params.put("push_menu", push_menu);
        params.put("id_device", device_id);

        final int _push_menu = push_menu;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                enableParametersPanel();
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.push_menu), _push_menu);
                                editor.commit();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        enableParametersPanel();
                        toggleBack();
                        Toast.makeText(getApplicationContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String auth_token = sharedPref.getString(getString(R.string.server_auth_token), "");
                params.put("Authorization", auth_token);

                return params;
            }
        };

        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    private void toggleBack() {
        if(getIntFromBoolean(notificationMenuInput.isChecked()) != push_menu) {
            notificationMenuInput.toggle();
        }
        if(getIntFromBoolean(notificationEventInput.isChecked()) != push_event) {
            notificationEventInput.toggle();
        }
        if(getIntFromBoolean(notificationOtherInput.isChecked()) != push_other) {
            notificationOtherInput.toggle();
        }
    }

    public int getIntFromBoolean(boolean b) {
        return b ? 1 : 0;
    }

    @Override
     public void changeUserPushConfig(int event, int other) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.59.123.110:443/users/pushs/";

        JSONObject params = new JSONObject();

        String token = sharedPref.getString("registration_id", "");

        params.put("token", token);
        params.put("os", "android");
        params.put("push_event", event);
        params.put("push_other", other);

        final int _push_event = event;
        final int _push_other = other;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST,
                        url,
                        params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                enableParametersPanel();
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.push_event), _push_event);
                                editor.putInt(getString(R.string.push_other), _push_other);
                                editor.commit();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        enableParametersPanel();
                        toggleBack();
                        Toast.makeText(getApplicationContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String auth_token = sharedPref.getString(getString(R.string.server_auth_token), "");
                params.put("Authorization", auth_token);

                return params;
            }
        };

        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public void deleteUser() throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://37.59.123.110:443/users/";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.server_auth_token), "");
                                editor.putInt(getString(R.string.push_event), 0);
                                editor.putInt(getString(R.string.push_other), 0);
                                editor.commit();
                                deleteAccountButton.setVisibility(View.GONE);
                                notificationEventInput.setChecked(false);
                                notificationOtherInput.setChecked(false);
                                notificationEventInput.setEnabled(false);
                                notificationOtherInput.setEnabled(false);
                                Toast.makeText(getApplicationContext(), "Compte désactivé avec succès", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String auth_token = sharedPref.getString(getString(R.string.server_auth_token), "");
                params.put("Authorization", auth_token);

                return params;
            }
        };

        jsObjRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }

    public void onClickDeleteAccount(View view) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Etes-vous sûr ?")
                .setConfirmText("Oui")
                .setCancelText("Non")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        try {
                            deleteUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }
}