package programminglife.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import programminglife.model.Bookmark;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by marti_000 on 15-5-2017.
 */
public class BookmarkController {
    private static String BOOKMARKPATH = "bookmarks.xml";

    /**
     * Load a bookmark based on the graph's name and bookmark's name.
     * @param graphName is the name of the graph in which this bookmark exists.
     * @param bookMarkName is the name of the bookmark (needs to be unique for bookmarks in the same graph).
     * @return the {@link Bookmark} you are trying to load.
     */
    public static Bookmark loadBookmark(String graphName, String bookMarkName) {
        Document dom;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(BOOKMARKPATH);
            Element doc = dom.getDocumentElement();
            return getBookmark(doc, graphName, bookMarkName);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Searches through the dom to find the requested bookmark.
     * @param doc the document element.
     * @param graphName the name of the graph.
     * @param bookmarkName the name of the bookmark
     * @return the {@link Bookmark} with the requested name and graph.
     */
    private static Bookmark getBookmark(Element doc, String graphName, String bookmarkName) {
        NodeList nodeList = doc.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    NodeList bookmarks = nodeList.item(i).getChildNodes();
                    Element element = (Element) nodeList.item(i);
                    if (element.getNodeName().equals(graphName)) {
                        return new Bookmark(Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent()),
                                Integer.parseInt(element.getElementsByTagName("radius").item(0).getTextContent()),
                                element.getElementsByTagName("name").item(0).getTextContent(),
                                element.getElementsByTagName("description").item(0).getTextContent());
                    }

                }
            }
        }
        return null;
    }

    public static void storeBookmark(String graphName, String bookMarkName, String description, int nodeID, int radius) {

    }
}
