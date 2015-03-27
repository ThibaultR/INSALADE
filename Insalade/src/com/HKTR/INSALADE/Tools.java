package com.HKTR.INSALADE;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class Tools {
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    static String getWeekNumberFromPattern(String s, String pattern){
        Pattern p;
        Matcher m;
        String weekNumString = "";
        p = Pattern.compile(pattern);
        m = p.matcher(s);
        while (m.find()) {
            weekNumString = m.group(1);
        }
        return weekNumString;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
