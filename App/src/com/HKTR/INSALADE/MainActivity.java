package com.HKTR.INSALADE;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    LinearLayout dayList;
    Typeface fontExistenceLight;
    FrameLayout lastDayView;
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
        getMenus("menu42");

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

            for (int i = 0; i<nbRacineNoeuds; i++) {
                if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element menu = (Element) racineNoeuds.item(i);

                    String date = menu.getAttribute("date");
                    String dateStr = "";
                    String dateInt = "";
                    Pattern p;
                    Matcher m;
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
                        FrameLayout dayView = (FrameLayout) getLayoutInflater().inflate(R.layout.dayview_template, dayList, false);
                        Button dayButton = (Button) dayView.findViewWithTag("dayButton");
                        Button lunchButton = (Button) dayView.findViewWithTag("lunchButton");
                        Button dinnerButton = (Button) dayView.findViewWithTag("dinnerButton");

                        dayButton.setText(dateStr + " " + dateInt);
                        dayButton.setTypeface(fontExistenceLight);

                        lunchButton.setText(dateStr + " midi");
                        lunchButton.setTypeface(fontExistenceLight);

                        dinnerButton.setText(dateStr + " soir");
                        dinnerButton.setTypeface(fontExistenceLight);

                        dayList.addView(dayView);
                    }
                    //print a menu
                    System.out.println("\n*************MENU************");
                    System.out.println("date : " + menu.getAttribute("date"));
                    System.out.println("when : " + menu.getAttribute("when"));

                    final Element starter = (Element) menu.getElementsByTagName("starter").item(0);
                    final Element mainCourse = (Element) menu.getElementsByTagName("maincourse").item(0);
                    final Element dessert = (Element) menu.getElementsByTagName("dessert").item(0);

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
        if( lastDayView != null ) {
            Button lunchButton = (Button) lastDayView.findViewWithTag("lunchButton");
            Button dinnerButton = (Button) lastDayView.findViewWithTag("dinnerButton");
            lunchButton.setVisibility(View.GONE);
            dinnerButton.setVisibility(View.GONE);
        }

        FrameLayout dayView = (FrameLayout) view.getParent();
        lastDayView = dayView;
        Button lunchButton = (Button) dayView.findViewWithTag("lunchButton");
        Button dinnerButton = (Button) dayView.findViewWithTag("dinnerButton");
        lunchButton.setVisibility(View.VISIBLE);
        dinnerButton.setVisibility(View.VISIBLE);
    }
}
