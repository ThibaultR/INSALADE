package com.HKTR.INSALADE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class XmlFileGetter {
    static int weekNumber = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

    public static String[] getUrls(Context context) {
        ArrayList<String> urls = new ArrayList<String>();
        String[]  existingFiles = context.getFilesDir().list();
        ArrayList existingFilesList = new ArrayList(Arrays.asList(existingFiles));
        String URL = "http://lapiolco.fr/Symfony/web/XmlMenus/menu";

        for(int i = weekNumber-1; i<weekNumber+3; i++){
            if(!(existingFilesList.contains("menu"+i))) {
                urls.add(URL + i + ".xml");
            }
        }

        return urls.toArray(new String[urls.size()]);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    static class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            for(int i = 0 ; i < sUrl.length; i++) {
                try {
                    // Create a URL for the desired page
                    URL url = new URL(sUrl[i]);

                    // Read all the text returned by the server
                    InputStream inputStream = url.openStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    String fileText = sb.toString();

                    // write in file
                    FileOutputStream fileOutputStream = context.openFileOutput("menu"+(weekNumber+i-1), Context.MODE_PRIVATE);
                    fileOutputStream.write(fileText.getBytes());
                    fileOutputStream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

    }
}
