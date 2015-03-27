package com.HKTR.INSALADE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.HKTR.INSALADE.XmlFileGetter.DownloadTask;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.HKTR.INSALADE.Tools.getWeekNumberFromPattern;
import static com.HKTR.INSALADE.Tools.isOnline;
import static com.HKTR.INSALADE.XmlFileGetter.getUrls;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class MainActivity extends BaseActivity {

    DayModel currentDay;
    WeekModel currentWeek;
    int currentWeekNumber = 0;

    int selectedPage;
    int triangleWidth;
    int triangleHeight;

    boolean canUpdateTriangle = false;

    PullRefreshLayout refreshLayout;
    PullRefreshLayout noMenuRefreshView;

    View triangle;

    LinearLayout navigationButtons = null;
    /**
     * The number of pages (Number of week times seven days times two menus by day).
     */
    private static int numPages = WeekModel.getWeekList().size() * 7 * 2; //TODO : care to the number of menun

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private int oldMenuIndex = 0;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "969355700051";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    Context context;

    String regid;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        context = getApplicationContext();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.length() == 0) {
                registerInBackground();
            }
        }
        else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        navigationButtons = (LinearLayout) findViewById(R.id.navigationButtonList);
        setTriangleWidth();
        changeTextViewFont((TextView) findViewById(R.id.insalade_logo), fontPacifico);

        initiateScrollRefresh();


        //Get a Tracker (should auto-report)
        ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void initiateScrollRefresh() {
        PullRefreshLayout.OnRefreshListener onRefreshListener = new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isOnline(getApplicationContext())) {
                            File[] listFiles = getFilesDir().listFiles();
                            for (File f : listFiles) {
                                f.delete();
                            }

                            onResume();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Connexion internet non disponible", Toast.LENGTH_LONG).show();
                        }

                        refreshLayout.setRefreshing(false);
                        noMenuRefreshView.setRefreshing(false);
                    }
                }, 5000);
            }
        };
        refreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        noMenuRefreshView = (PullRefreshLayout) findViewById(R.id.noMenuRefreshView);

        // listen refresh event
        refreshLayout.setOnRefreshListener(onRefreshListener);
        noMenuRefreshView.setOnRefreshListener(onRefreshListener);
    }

    private void setTriangleWidth() {
        triangle = findViewById(R.id.triangle);
        triangle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                triangle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                triangleWidth = triangle.getWidth();
                triangleHeight = triangle.getHeight();

                canUpdateTriangle = true;
                updateTrianglePosition(selectedPage);
                // Here you can get the size :)
            }
        });
    }

    private void fillNavigationButtons() {
        //Fill navigationButtons
        String[] dayNameList = getResources().getStringArray(R.array.dayNameList);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, XmlFileGetter.weekNumber);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        int mondayDayNumber = cal.get(Calendar.DAY_OF_MONTH);
        navigationButtons.removeAllViews();
        for (int i = 0; i < 7; i++) {
            RelativeLayout navigationButton = (RelativeLayout) getLayoutInflater().inflate(R.layout.day_button_fragment, navigationButtons, false);

            // Set Day name (Lun, Mar...)
            TextView dayName = (TextView) navigationButton.findViewById(R.id.dayName);
            dayName.setText(dayNameList[i]);

            // Set Day number
            TextView dayNumber = (TextView) navigationButton.findViewById(R.id.dayNumber);

            int actualDayNumber = mondayDayNumber + i;
            dayNumber.setText("" + actualDayNumber);

            changeTextViewFont(dayName, fontRobotoLight);
            changeTextViewFont(dayNumber, fontRobotoLight);

            // Set tickmarks color if closed
            DayModel currentDay = WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(i);
            if (currentDay.getLunch().isClosed()) {
                navigationButton.findViewById(R.id.tickMarkLunch).setBackgroundResource(R.drawable.closed_tickmark);
            }

            if (currentDay.getDinner().isClosed()) {
                navigationButton.findViewById(R.id.tickMarkDinner).setBackgroundResource(R.drawable.closed_tickmark);
            }

            navigationButtons.addView(navigationButton);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        try {
            if (isOnline(context)) {
                refreshServerToken();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (initiateFiles()) {
            // If there is a file
            toggleRefreshView(true);
            showNavigationButtonList();
            fillNavigationButtons();
            initiateViewPager();
            goToCurrentMenu();
            updateTrianglePosition(mPager.getCurrentItem());
        }
        else {
            toggleRefreshView(false);
        }
    }

    private void toggleRefreshView(boolean isMenuAvailable) {
        View noMenuRefreshView = findViewById(R.id.noMenuRefreshView);
        View swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        if (isMenuAvailable) {
            noMenuRefreshView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
        else {
            noMenuRefreshView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    private void showNavigationButtonList() {
        navigationButtons.setVisibility(View.VISIBLE);
    }

    private void initiateViewPager() {
        numPages = WeekModel.getWeekList().size() * 7 * 2;
        int indexPage = getIntent().getIntExtra("idPage", 0);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(indexPage);

        updateCurrentMenuIndicationColor(indexPage, false);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectedPage = i;
                updateCurrentMenuIndicationColor(i, true);
                updateTrianglePosition(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

        });
    }

    private void updateTrianglePosition(int i) {
        if (canUpdateTriangle) {
            int day = i / 2;
            Display display = getWindowManager().getDefaultDisplay();
            int marginUnit = display.getWidth() / 7;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(triangleWidth, triangleHeight);

            params.setMargins(marginUnit * day + (marginUnit - triangleWidth) / 2, 0, 0, 0);
            triangle.setLayoutParams(params);
        }
    }

    public boolean initiateFiles() {
        // Get menus from internet if possible
        boolean result = getXmlFiles();

        // Get menus list from intern storage
        File[] menus = getFilesDir().listFiles();
        Arrays.sort(menus);

        // Loop on all files to get content
        for (File f : menus) {
            String menuFileName = f.getName();
            // Get week number from the name of the xml file (ex : menu42 return 42)
            String weekNumString = getWeekNumberFromPattern(menuFileName, "([\\d]+)");

            if (weekNumString.length() > 0) {
                currentWeekNumber = Integer.valueOf(weekNumString);
                getMenus(menuFileName);
            }
        }

        return result;
    }

    public void getMenus(String file) {
        //Step 1 : getting instance of the class "DocumentBuilderFactory"
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        int menuNumber = 0;

        try {
            //Step 2 : creation of the parser
            final DocumentBuilder builder = factory.newDocumentBuilder();

            //Step 3 : getting the XML document
            InputStream xmlFile = openFileInput(file);
            final Document document = builder.parse(xmlFile);

            //Step 4 : getting the root element
            final Element racine = document.getDocumentElement();

            //Step 5 : getting menus
            final NodeList racineNoeuds = racine.getChildNodes();
            final int nbRacineNoeuds = racineNoeuds.getLength();

            currentWeek = new WeekModel();

            WeekModel.getWeekList().put(currentWeekNumber, currentWeek);

            for (int i = 0; i < nbRacineNoeuds; i++) {
                if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element menu = (Element) racineNoeuds.item(i);
                    final Element starter = (Element) menu.getElementsByTagName("starter").item(0);
                    final Element mainCourse = (Element) menu.getElementsByTagName("maincourse").item(0);
                    final Element dessert = (Element) menu.getElementsByTagName("dessert").item(0);

                    if (menu.getAttribute("when").equals("0")) {
                        currentDay = new DayModel();
                        currentDay.setWeekNumber(currentWeekNumber);
                        currentDay.setDayNumber(menuNumber++);

                        String starterContent = removeWhiteSpaces(starter.getTextContent());
                        String mainCourseContent = removeWhiteSpaces(mainCourse.getTextContent());
                        String dessertContent = removeWhiteSpaces(dessert.getTextContent());

                        MenuModel lunch = new MenuModel(menu.getAttribute("date"), starterContent, mainCourseContent, dessertContent);
                        currentDay.setLunch(lunch);
                    }
                    else {
                        String starterContent = removeWhiteSpaces(starter.getTextContent());
                        String mainCourseContent = removeWhiteSpaces(mainCourse.getTextContent());
                        String dessertContent = removeWhiteSpaces(dessert.getTextContent());

                        MenuModel dinner = new MenuModel(menu.getAttribute("date"), starterContent, mainCourseContent, dessertContent);

                        currentDay.setDinner(dinner);
                        currentWeek.getWeek().add(currentDay);

                    }
                }
            }
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getXmlFiles() {
        String[] urls = getUrls(getApplicationContext());

        // Execute File Downloader
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute(urls);

        int k = 0;
        while (!(new ArrayList(Arrays.asList(getFilesDir().list()))).contains("menu" + XmlFileGetter.weekNumber) && k < 10) {
            try {
                downloadTask.get(1000, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
            catch (TimeoutException e) {
                e.printStackTrace();
            }

            k++;
        }

        // Remove old files
        File[] listFiles = getFilesDir().listFiles();
        for (File listFile : listFiles) {

            String number = getWeekNumberFromPattern(listFile.getName(), "([\\d]+)");

            if (number.length() > 0) {
                // if it's not the current week or the next week, we delete
                if (XmlFileGetter.weekNumber != Integer.valueOf(number) && (XmlFileGetter.weekNumber + 1) % 52 != Integer.valueOf(number)) {
                    listFile.delete();
                }
                // is currentWeekMenu present
                if (Integer.valueOf(number) == XmlFileGetter.weekNumber) {
                    XmlFileGetter.currentWeekMenu = true;
                }
            }
        }


        // Toast if current Week Menu hasn't been downloaded
        if (!XmlFileGetter.currentWeekMenu) {
            if (isOnline(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Menu de la semaine non disponible sur l'intranet...", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Menu de la semaine non disponible, vérifiez votre connexion", Toast.LENGTH_LONG).show();
            }

            return false;
        }
        else {
            return true;
        }
    }

    public String removeWhiteSpaces(String input) {
        String result = "";
        String[] lines = input.split("\\n");
        for (String line : lines) {
            if (line.trim().length() > 1) {
                result += (line.trim() + "\n");
            }
        }

        //removes the last '\n'
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SlideMenuFragment.create(position);
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }

    // TODO Factoriser
    public void updateCurrentMenuIndicationColor(int pageIndex, boolean updateOldDayButton) {
        if (updateOldDayButton) {
            RelativeLayout oldNavigationButton = (RelativeLayout) navigationButtons.getChildAt(oldMenuIndex / 2);
            if (oldMenuIndex % 2 == 0) {
                View tickMarkLunch = oldNavigationButton.findViewById(R.id.tickMarkLunch);
                if (WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(oldMenuIndex / 2).getLunch().isClosed()) {
                    tickMarkLunch.setBackgroundResource(R.drawable.closed_tickmark);
                }
                else {
                    tickMarkLunch.setBackgroundResource(R.drawable.default_tickmark);
                }
            }
            else {
                View tickMarkDinner = oldNavigationButton.findViewById(R.id.tickMarkDinner);
                if (WeekModel.getWeekList().get(XmlFileGetter.weekNumber).getWeek().get(oldMenuIndex / 2).getDinner().isClosed()) {
                    tickMarkDinner.setBackgroundResource(R.drawable.closed_tickmark);
                }
                else {
                    tickMarkDinner.setBackgroundResource(R.drawable.default_tickmark);
                }
            }

            ((TextView) oldNavigationButton.findViewById(R.id.dayNumber)).setTextColor(getResources().getColor(R.color.defaultDayButtonColor));
            ((TextView) oldNavigationButton.findViewById(R.id.dayName)).setTextColor(getResources().getColor(R.color.defaultDayButtonColor));
        }

        RelativeLayout navigationButton = (RelativeLayout) navigationButtons.getChildAt(pageIndex / 2);
        if (pageIndex % 2 == 0) {
            navigationButton.findViewById(R.id.tickMarkLunch).setBackgroundResource(R.drawable.selected_tickmark);
        }
        else {
            navigationButton.findViewById(R.id.tickMarkDinner).setBackgroundResource(R.drawable.selected_tickmark);
        }
        ((TextView) navigationButton.findViewById(R.id.dayNumber)).setTextColor(getResources().getColor(R.color.activeDayButtonColor));
        ((TextView) navigationButton.findViewById(R.id.dayName)).setTextColor(getResources().getColor(R.color.activeDayButtonColor));

        oldMenuIndex = pageIndex;
    }

    public void onClickNavigationButton(View view) {
        int i;
        for (i = 0; i < navigationButtons.getChildCount(); i++) {
            RelativeLayout navigationButton = (RelativeLayout) navigationButtons.getChildAt(i);
            if (view.equals(navigationButton)) {
                break;
            }
        }

        if (mPager.getCurrentItem() == i * 2) {
            mPager.setCurrentItem(i * 2 + 1, false);
        }
        else {
            mPager.setCurrentItem(i * 2, false);
        }
    }

    public void onClickHeaderText(View view) {
        goToCurrentMenu();
    }

    public void onClickEventButton(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

    public void onClickMenu(View view) {
        Intent intent = new Intent(this, ParametersActivity.class);
        startActivity(intent);
    }

    public void goToCurrentMenu() {
        //Sunday = 1, Monday = 2 ...
        Calendar now = Calendar.getInstance();
        int today = (now.get(Calendar.DAY_OF_WEEK) - 2) % 7;
        int timeH = now.get(Calendar.HOUR_OF_DAY);

        int menuNumber;
        //afficher le déjeuner
        if (timeH < 14) {
            menuNumber = today * 2;
        }
        //afficher le dinner
        else {
            menuNumber = today * 2 + 1;
        }

        mPager.setCurrentItem(menuNumber, false);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                                                      PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Log.i("MainActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                                             .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                }
                catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context
     *         application's context.
     * @param regId
     *         registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /*
     * To get from the server a new token for events
     */
    private void refreshServerToken() throws JSONException {
        if (isOnline(context)) {
            final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            String url = "http://37.59.123.110:443/sessions/";
            RequestQueue queue = Volley.newRequestQueue(this);

            String device_id = sharedPref.getString(getString(R.string.device_id), "");

            JSONObject params = new JSONObject();
            params.put("id_device", device_id);

            String email = sharedPref.getString(getString(R.string.saved_email), "");
            final String password = sharedPref.getString(getString(R.string.server_password), "");

            params.put("mail", email);
            params.put("password", password);
            params.put("os", "android");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST,
                     url,
                     params,
                     new Response.Listener<JSONObject>() {
                         @Override
                         public void onResponse(JSONObject response) {
                             try {
                                 SharedPreferences.Editor editor = sharedPref.edit();
                                 editor.putString(getString(R.string.server_auth_token), response.getString("token"));
                                 editor.commit();
                             }
                             catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }
                     }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.server_auth_token), "");
                            editor.commit();
                        }
                    });

            jsObjRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsObjRequest);
        }
    }
}
