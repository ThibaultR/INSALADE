package com.HKTR.INSALADE;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
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
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hyukchan Kwon (hyukchan.k@gmail.com)
 * @author Thibault Rapin (thibault.rapin@gmail.com)
 */
public class MainActivity extends Activity {
    LinearLayout dayList;
    Typeface fontExistenceLight;
    FrameLayout lastDayView;
    DayModel currentDay;
    WeekModel currentWeek;
    Integer currentWeekNumber;
    Integer menuNumber;

    FrameLayout currentDayView;
    Button currentDayButton;
    Button currentLunchButton;
    Button currentDinnerButton;

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
        menuNumber = 0;


        // Get menus from res/raw folder
        Field[] menus = R.raw.class.getFields();

        for(Field f : menus) {
            String menuFileName = f.getName();
            // Get week number from the name of the xml file (ex : menu42 return 42)
            Pattern p;
            Matcher m;
            String weekNumString = "";
            p = Pattern.compile("([\\d]+)");
            m = p.matcher(menuFileName);
            while (m.find()) {
                weekNumString = m.group(1);
            }
            currentWeekNumber = Integer.valueOf(weekNumString);

            getMenus(menuFileName);
        }

    }

    public void getMenus(String file) {
        //Step 1 : getting instance of the class "DocumentBuilderFactory"
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            //Step 2 : creation of the parser
            final DocumentBuilder builder = factory.newDocumentBuilder();

            //Step 3 : getting the XML document
            int fileId = getResources().getIdentifier(file, "raw", this.getPackageName());
            final Document document= builder.parse(getResources().openRawResource(fileId));

            //Affiche du prologue
            System.out.println("*************PROLOGUE************");
            System.out.println("version : " + document.getXmlVersion());
            System.out.println("encodage : " + document.getXmlEncoding());
            System.out.println("standalone : " + document.getXmlStandalone());

            //Step 4 : getting the root element
            final Element racine = document.getDocumentElement();

            //Print the root element
            System.out.println("\n*************RACINE************");
            System.out.println(racine.getNodeName());

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
                    String dateInt = "";
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
                        p = Pattern.compile("([\\d]+)");
                        m = p.matcher(date);
                        while (m.find()) { // Find each match in turn; String can't do this.
                            dateInt = m.group(1); // Access a submatch group; String can't do this.
                        }
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

                    //print a menu
                    System.out.println("\n*************MENU************");
                    System.out.println("date : " + menu.getAttribute("date"));
                    System.out.println("when : " + menu.getAttribute("when"));

                    //print starter, mainCourse and dessert
                    System.out.println("starer : " + starter.getTextContent());
                    System.out.println("mainCourse : " + mainCourse.getTextContent());
                    System.out.println("dessert : " + dessert.getTextContent());
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
        FrameLayout dayView = (FrameLayout) view.getParent();

        ImageView dayButtonClosedIcon = (ImageView) dayView.findViewWithTag("dayButtonClosedIcon");

        //if it is closed for that day, than do not react at the click
        if(dayButtonClosedIcon == null) {
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
    }


    public void onClickLunchButton(View view) {
        FrameLayout dayView = (FrameLayout) view.getParent();
        ImageView lunchButtonClosedIcon = (ImageView) dayView.findViewWithTag("lunchButtonClosedIcon");

        //if it is closed for that lunch, than do not react at the click
        if(lunchButtonClosedIcon == null) {
            WeekModel.setCurrentMenuId(view.getId());
            WeekModel.setCurrentMenuIsLunch(true);
            Intent intent = new Intent(this, SlideMenuActivity.class);
            intent.putExtra("idPage", view.getId()*2);
            startActivity(intent);
        }
    }

    public void onClickDinnerButton(View view) {
        FrameLayout dayView = (FrameLayout) view.getParent();
        ImageView dinnerButtonClosedIcon = (ImageView) dayView.findViewWithTag("dinnerButtonClosedIcon");

        //if it is closed for that lunch, than do not react at the click
        if(dinnerButtonClosedIcon == null) {
            WeekModel.setCurrentMenuId(view.getId());
            WeekModel.setCurrentMenuIsLunch(false);
            Intent intent = new Intent(this, SlideMenuActivity.class);
            intent.putExtra("idPage", view.getId() * 2 + 1);
            startActivity(intent);
        }
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
}
