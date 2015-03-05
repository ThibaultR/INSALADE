import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXMLFile {
    public static void main(final String[] args) {
        /*
         * Step 1 : getting instance of the class "DocumentBuilderFactory"
         */
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                
        try {
            /*
             * Step 2 : creation of the parser
             */
            final DocumentBuilder builder = factory.newDocumentBuilder();

            /*
             * Step 3 : getting the XML document
             */
            final Document document= builder.parse(new File("menu42.xml"));

            //Affiche du prologue
            System.out.println("*************PROLOGUE************");
            System.out.println("version : " + document.getXmlVersion());
            System.out.println("encodage : " + document.getXmlEncoding());
            System.out.println("standalone : " + document.getXmlStandalone());

            /*
             * Step 4 : getting the root element
             */
            final Element racine = document.getDocumentElement();

            //Print the root element
            System.out.println("\n*************RACINE************");
            System.out.println(racine.getNodeName());

            /*
             * Step 5 : getting menus
             */
            final NodeList racineNoeuds = racine.getChildNodes();
            final int nbRacineNoeuds = racineNoeuds.getLength();

            for (int i = 0; i<nbRacineNoeuds; i++) {
                if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element menu = (Element) racineNoeuds.item(i);
                    //print a menu
                    System.out.println("\n*************MENU************");
                    System.out.println("date : " + menu.getAttribute("date"));
                    System.out.println("when : " + menu.getAttribute("when"));
                    /*
                     * Step 6 : getting starter, mainCourse and dessert
                     */
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
}