package com.HKTR.INSALADE;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.HKTR.INSALADE.model.DayModel;
import com.HKTR.INSALADE.model.MenuModel;
import com.HKTR.INSALADE.model.WeekModel;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.HKTR.INSALADE.XmlFileGetter.*;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class MainActivity extends Activity {
    LinearLayout dayList;
    Typeface fontExistenceLight;
    Typeface latoBold;
    FrameLayout lastDayView;
    DayModel currentDay;
    WeekModel currentWeek;
    Integer currentWeekNumber = 0;
    Integer menuNumber;

    FrameLayout currentDayView;
    Button currentDayButton;
    Button currentLunchButton;
    Button currentDinnerButton;

    TextView weekSeparatorView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dayList = (LinearLayout) findViewById(R.id.dayList);
        fontExistenceLight = Typeface.createFromAsset(getAssets(), "fonts/Existence-Light.otf");
        latoBold = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
        menuNumber = 0;
    }


    @Override protected void onStart() {
        menuNumber = 0;
        Log.e("start", "start");
        super.onResume();
        dayList.removeAllViews();
        // Get menus from internet if possible
        getXmlFiles();

        // Get menus list from intern storage
        File[] menus = getFilesDir().listFiles();
        Arrays.sort(menus);

        Boolean isFirstWeek = true;
        // Loop on all files to get content
        for(File f : menus) {
            Log.e("plop", f.getName());
            String menuFileName = f.getName();
            // Get week number from the name of the xml file (ex : menu42 return 42)
            String weekNumString = getWeekNumberFromPattern(menuFileName, "([\\d]+)");

            if (weekNumString.length() > 0) {
                currentWeekNumber = Integer.valueOf(weekNumString);
                //add WeekSeparator

                weekSeparatorView = (TextView) getLayoutInflater().inflate(R.layout.weekseparator_template, dayList, false);
                weekSeparatorView.setTypeface(latoBold);
                if (isFirstWeek){
                    weekSeparatorView.setText("Cette semaine");
                    isFirstWeek = false;
                } else {
                    weekSeparatorView.setText("Semaine "+currentWeekNumber);
                }
                dayList.addView(weekSeparatorView);

                getMenus(menuFileName);
            }
        }

        final FrameLayout todayButton = (FrameLayout) dayList.findViewWithTag("today");
        if(todayButton != null) {
            final ScrollView scrollView = (ScrollView) dayList.getParent();
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, todayButton.getTop());
                }
            });
        }
    }

    @Override protected void onResume() {
        super.onResume();
        Log.e("resume","resume");
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

            for (int i = 0; i<nbRacineNoeuds; i++) {
                if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
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

                        currentDayView = (FrameLayout) getLayoutInflater().inflate(R.layout.dayview_template, dayList, false);

                        currentDayButton = (Button) currentDayView.findViewWithTag("dayButton");
                        currentLunchButton = (Button) currentDayView.findViewWithTag("lunchButton");
                        currentDinnerButton = (Button) currentDayView.findViewWithTag("dinnerButton");

                        currentDay = new DayModel();
                        currentDay.setWeekNumber(currentWeekNumber);
                        currentDay.setDayNumber(menuNumber); //TODO : why ? o_O

                        String starterContent = removeWhiteSpaces(starter.getTextContent());
                        String mainCourseContent = removeWhiteSpaces(mainCourse.getTextContent());
                        String dessertContent = removeWhiteSpaces(dessert.getTextContent());

                        MenuModel lunch = new MenuModel(menu.getAttribute("date"), starterContent, mainCourseContent, dessertContent);


                        currentDayButton.setText(dateStr + " " + dateInt);
                        currentDayButton.setTypeface(fontExistenceLight);

                        currentLunchButton.setText(dateStr + " midi");
                        currentLunchButton.setTypeface(fontExistenceLight);
                        currentLunchButton.setId(menuNumber);

                        currentDinnerButton.setText(dateStr + " soir");
                        currentDinnerButton.setTypeface(fontExistenceLight);
                        currentDinnerButton.setId(menuNumber++);

                        //if current day is today, set a tag for default scroll
                        DateFormat df = new SimpleDateFormat("dd");
                        Date today = new Date();
                        if(df.format(today).equals(dateInt)) {
                            currentDayView.setTag("today");
                            currentDayButton.setText("Aujourd'hui");
                            currentLunchButton.setText("Ce midi");
                            currentDinnerButton.setText("Ce soir");
                        }

                        dayList.addView(currentDayView);

                        currentDay.setLunch(lunch);
                    } else {

                        String starterContent = removeWhiteSpaces(starter.getTextContent());
                        String mainCourseContent = removeWhiteSpaces(mainCourse.getTextContent());
                        String dessertContent = removeWhiteSpaces(dessert.getTextContent());

                        MenuModel dinner = new MenuModel(menu.getAttribute("date"), starterContent, mainCourseContent, dessertContent);

                        currentDay.setDinner(dinner);
                        currentWeek.getWeek().add(currentDay);

                        //adds "closed" icons if there is no menu


                        if(currentDay.isClosed()) {
                            ImageView closed = (ImageView) getLayoutInflater().inflate(R.layout.iconclosed_template, currentDayView, false);
                            closed.setTag("dayButtonClosedIcon");
                            closed.setVisibility(View.VISIBLE);
                            currentDayView.addView(closed);
                        }

                        if(currentDay.getLunch().isClosed()) {
                            ImageView closedLunch = (ImageView) getLayoutInflater().inflate(R.layout.iconclosed_template, currentDayView, false);
                            closedLunch.setTag("lunchButtonClosedIcon");
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) closedLunch.getLayoutParams();
                            params. bottomMargin = dpToPx(50);
                            closedLunch.setLayoutParams(params);
                            currentDayView.addView(closedLunch);
                        }

                        if(currentDay.getDinner().isClosed()) {
                            ImageView closedDinner = (ImageView) getLayoutInflater().inflate(R.layout.iconclosed_template, currentDayView, false);
                            closedDinner.setTag("dinnerButtonClosedIcon");
                            closedDinner.setBackgroundColor(getResources().getColor(R.color.tileBackgroundDarker));
                            currentDayView.addView(closedDinner);
                        }
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

    public void onClickDayButton(View view) {
        if (lastDayView != null) {
            Button lunchButton = (Button) lastDayView.findViewWithTag("lunchButton");
            Button dinnerButton = (Button) lastDayView.findViewWithTag("dinnerButton");
            lunchButton.setVisibility(View.GONE);
            dinnerButton.setVisibility(View.GONE);

            ImageView lunchButtonClosedIcon = (ImageView) lastDayView.findViewWithTag("lunchButtonClosedIcon");
            if (lunchButtonClosedIcon != null) {
                lunchButtonClosedIcon.setVisibility(View.GONE);
            }
            ImageView dinnerButtonClosedIcon = (ImageView) lastDayView.findViewWithTag("dinnerButtonClosedIcon");
            if (dinnerButtonClosedIcon != null) {
                dinnerButtonClosedIcon.setVisibility(View.GONE);
            }
        }

        FrameLayout dayView = (FrameLayout) view.getParent();

        lastDayView = dayView;
        Button lunchButton = (Button) dayView.findViewWithTag("lunchButton");
        Button dinnerButton = (Button) dayView.findViewWithTag("dinnerButton");
        lunchButton.setVisibility(View.VISIBLE);

        ImageView lunchButtonClosedIcon = (ImageView) dayView.findViewWithTag("lunchButtonClosedIcon");
        if (lunchButtonClosedIcon != null) {
            lunchButtonClosedIcon.setVisibility(View.VISIBLE);
        }
        dinnerButton.setVisibility(View.VISIBLE);

        ImageView dinnerButtonClosedIcon = (ImageView) dayView.findViewWithTag("dinnerButtonClosedIcon");
        if (dinnerButtonClosedIcon != null) {
            dinnerButtonClosedIcon.setVisibility(View.VISIBLE);
        }
    }


    public void onClickLunchButton(View view) {
        WeekModel.setCurrentMenuId(view.getId());
        WeekModel.setCurrentMenuIsLunch(true);
        Intent intent = new Intent(this, SlideMenuActivity.class);
        intent.putExtra("idPage", view.getId()*2);
        startActivity(intent);
    }

    public void onClickDinnerButton(View view) {
        WeekModel.setCurrentMenuId(view.getId());
        WeekModel.setCurrentMenuIsLunch(false);
        Intent intent = new Intent(this, SlideMenuActivity.class);
        intent.putExtra("idPage", view.getId() * 2 + 1);
        startActivity(intent);
    }

    public String removeWhiteSpaces(String input) {
        String result = "";
        String[] lines = input.split("\\n");
        for (String line : lines) {
            if(line.trim().length() > 1) {
                result += (line.trim() + "\n");
            }
        }

        //removes the last '\n'
        if(result.length() > 0)
            result = result.substring(0, result.length()-1);

        return result;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void onClickHeaderLogo(View view) {
        final FrameLayout todayButton = (FrameLayout) dayList.findViewWithTag("today");
        if(todayButton != null) {
            final ScrollView scrollView = (ScrollView) dayList.getParent();
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, todayButton.getTop());
                }
            });
        }
    }

    public void getXmlFiles(){
        String[] urls = getUrls(getApplicationContext());

        // Execute File Downloader
        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
        downloadTask.execute(urls);

        int k = 0;
        while(! (new ArrayList(Arrays.asList(getFilesDir().list()))).contains("menu"+weekNumber) && k<10){
            try {
                downloadTask.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            k++;
        }

        // Remove old files
        File[] listFiles = getFilesDir().listFiles();
        for(int i = 0; i<listFiles.length;i++) {

            String number = getWeekNumberFromPattern(listFiles[i].getName(), "([\\d]+)");

            if(number.length() > 0){
                // if old -> remove
                if( Integer.valueOf(number) < weekNumber ) {
                    listFiles[i].delete();
                }
                // is currentWeekMenu present
                if(Integer.valueOf(number) == weekNumber) {
                    currentWeekMenu = true;
                }
            }
        }


        // Toast if current Week Menu hasn't been downloaded
        if(!currentWeekMenu){
            if(isOnline(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "Menu de la semaine non disponible sur l'intranet...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Menu de la semaine non disponible, vérifiez votre connexion", Toast.LENGTH_LONG).show();
            }

        }
    }
}
