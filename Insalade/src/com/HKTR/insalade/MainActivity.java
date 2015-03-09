package com.HKTR.insalade;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.HKTR.insalade.XmlFileGetter.DownloadTask;
import com.HKTR.insalade.model.DayModel;
import com.HKTR.insalade.model.MenuModel;
import com.HKTR.insalade.model.WeekModel;
import com.baoyz.widget.PullRefreshLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.HKTR.insalade.Tools.getWeekNumberFromPattern;
import static com.HKTR.insalade.Tools.isOnline;
import static com.HKTR.insalade.XmlFileGetter.getUrls;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class MainActivity extends FragmentActivity {

    DayModel currentDay;
    WeekModel currentWeek;
    int currentWeekNumber = 0;
    int menuNumber = 0;

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        navigationButtons = (LinearLayout) findViewById(R.id.navigationButtonList);
        setTriangleWidth();
        changeHeaderFont();

        initiateScrollRefresh();

        //test to remove
        //Intent intent = new Intent(this, EventInscriptionActivity.class);
        //startActivity(intent);
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

    private void changeHeaderFont() {
        Typeface fontPacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        TextView headerMenuTitle = (TextView) findViewById(R.id.insalade_logo);
        headerMenuTitle.setTypeface(fontPacifico);
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

        navigationButtons.removeAllViews();
        for (int i = 0; i < 7; i++) {
            RelativeLayout navigationButton = (RelativeLayout) getLayoutInflater().inflate(R.layout.day_button_fragment, navigationButtons, false);

            // Set Day name (Lun, Mar...)
            TextView dayName = (TextView) navigationButton.findViewById(R.id.dayName);
            dayName.setText(dayNameList[i]);

            // Set Day number
            TextView dayNumber = (TextView) navigationButton.findViewById(R.id.dayNumber);
            cal.set(Calendar.DAY_OF_WEEK, (i + 2) % 7);
            dayNumber.setText("" + cal.get(Calendar.DAY_OF_MONTH));

            Typeface fontLatoLight = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
            dayName.setTypeface(fontLatoLight);
            dayNumber.setTypeface(fontLatoLight);

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
        menuNumber = 0;
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

                    String date = menu.getAttribute("date");
                    String dateStr = "";
                    String dateInt;
                    Pattern p;
                    Matcher m;

                    final Element starter = (Element) menu.getElementsByTagName("starter").item(0);
                    final Element mainCourse = (Element) menu.getElementsByTagName("maincourse").item(0);
                    final Element dessert = (Element) menu.getElementsByTagName("dessert").item(0);

                    if (menu.getAttribute("when").equals("0")) {

                        // get date string (ex: Lundi)
                        p = Pattern.compile("([a-zA-Z]+)");
                        m = p.matcher(date);
                        while (m.find()) { // Find each match in turn; String can't do this.
                            dateStr = m.group(1); // Access a submatch group; String can't do this.
                        }

                        // get date int (ex: 12)
                        dateInt = getWeekNumberFromPattern(date, "([\\d]+)");

                        currentDay = new DayModel();
                        currentDay.setWeekNumber(currentWeekNumber);
                        currentDay.setDayNumber(menuNumber++); //TODO : why ? o_O

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
        for (int i = 0; i < listFiles.length; i++) {

            String number = getWeekNumberFromPattern(listFiles[i].getName(), "([\\d]+)");

            if (number.length() > 0) {
                // if it's not the current week or the next week, we delete
                if (XmlFileGetter.weekNumber != Integer.valueOf(number) && (XmlFileGetter.weekNumber + 1) % 52 != Integer.valueOf(number)) {
                    listFiles[i].delete();
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

    public void onClickMenu(View view) {
        Log.e("TestOnClickMenu", "Works");
    }

    public void onClickEventInscription(View view) {
        Intent intent = new Intent(this, EventInscriptionActivity.class);
        startActivity(intent);
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

    //TODO TO REMOVE
    public void onCLickEventButton(View view) {
        Intent intent = new Intent(this, EventActivity.class);
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

}
