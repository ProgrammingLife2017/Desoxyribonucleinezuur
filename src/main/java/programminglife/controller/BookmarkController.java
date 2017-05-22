package programminglife.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import programminglife.model.Bookmark;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martijn van Meerten on 15-5-2017.
 * This class is a controller for loading storing and deleting bookmarks.
 */
public final class BookmarkController {
    private static final String BOOKMARKPATH = "bookmarks.xml";

    /**
     * This class should not be instantiated.
     * The functionality of this class does not rely on any particular instance.
     * It relies solely on the contents of the bookmarks file.
     */
    private BookmarkController() { }

    /**
     * Default bookmark loading method.
     * Uses the default bookmark path.
     * @param graphName is the name of the graph int which this bookmark exists.
     * @param bookMarkName is the name of the bookmark (needs to be unique for bookmarks in the same graph).
     * @return the {@link Bookmark} you are trying to load.
     */
    public static Bookmark loadBookmark(String graphName, String bookMarkName) {
        return loadBookmark(BOOKMARKPATH, graphName, bookMarkName);
    }

    /**
     * Default all bookmark loading method.
     * Uses the default bookmark path
     * @param graphName The name of the graph from which to get the bookmarks.
     * @return A List containing all bookmarks.
     */
    public static List<Bookmark> loadAllGraphBookmarks(String graphName) {
        return loadAllGraphBookmarks(BOOKMARKPATH, graphName);
    }

    /**
     * Default bookmark storing method.
     * Uses the default bookmark path.
     * @param graphName The name of the graph file.
     * @param bookMarkName The name of the bookmark (must be unique for the graph).
     * @param description The description of this bookmark.
     * @param nodeID The ID of the node for searching.
     * @param radius The radius of the bookmark for searching.
     */
    public static void storeBookmark(String graphName, String bookMarkName,
                                     String description, int nodeID, int radius) {
        storeBookmark(BOOKMARKPATH, graphName, bookMarkName, description, nodeID, radius);
    }

    /**
     * Default bookmark storing method.
     * Uses the default bookmark path.
     * @param graphName The name of the graph file.
     * @param bookMarkName The name of the bookmark (must be unique for the graph).
     */
    public static void deleteBookmark(String graphName, String bookMarkName) {
        deleteBookmark(BOOKMARKPATH, graphName, bookMarkName);
    }

    /**
     * Load a bookmark based on the graph's name and bookmark's name.
     * @param fileName is the path of the bookmarks file location
     * @param graphName is the name of the graph in which this bookmark exists.
     * @param bookMarkName is the name of the bookmark (needs to be unique for bookmarks in the same graph).
     * @return the {@link Bookmark} you are trying to load.
     */
    public static Bookmark loadBookmark(String fileName, String graphName, String bookMarkName) {
        checkFile(fileName);
        Document dom;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(fileName);
            dom.getDocumentElement().normalize();
            Element doc = dom.getDocumentElement();
            return getBookmark(doc, graphName, bookMarkName);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be loaded.", ButtonType.CLOSE)).show();
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
        NodeList nodeList = doc.getElementsByTagName("graph");
        Element graph = findTag(nodeList, graphName);
        if (graph != null) {
            NodeList bookmarks = graph.getElementsByTagName("bookmark");
            Element el = findTag(bookmarks, bookmarkName);
            if (el == null) {
                return null;
            } else {
                return parseBookmark(el);
            }
        }
        return null;
    }

    /**
     * Store a bookmark in bookmarks.xml.
     * @param fileName The path of the bookmarks file.
     * @param graphName The name of the graph file.
     * @param bookMarkName The name of the bookmark (must be unique for the graph.
     * @param description The description of this bookmark.
     * @param nodeID The ID of the node for searching.
     * @param radius The radius of the bookmark for searching.
     */
    public static void storeBookmark(String fileName, String graphName, String bookMarkName,
                                     String description, int nodeID, int radius) {
        checkFile(fileName);
        if (loadBookmark(fileName, graphName, bookMarkName) != null) {
            return;
        }
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(fileName);
            doc.getDocumentElement().normalize();
            Element rootElement = doc.getDocumentElement();

            Element newBookmark = doc.createElement("bookmark");
            Element nameTag = doc.createElement("name");
            nameTag.appendChild(doc.createTextNode(bookMarkName));
            Element descriptionTag = doc.createElement("description");
            descriptionTag.appendChild(doc.createTextNode(description));
            Element radiusTag = doc.createElement("radius");
            radiusTag.appendChild(doc.createTextNode(String.valueOf(radius)));
            Element iDTag = doc.createElement("ID");
            iDTag.appendChild(doc.createTextNode(String.valueOf(nodeID)));

            newBookmark.appendChild(nameTag);
            newBookmark.appendChild(iDTag);
            newBookmark.appendChild(radiusTag);
            newBookmark.appendChild(descriptionTag);

            Element graphTag = findTag(doc.getElementsByTagName("graph"), graphName);

            // No earlier bookmarks in this graph
           if (graphTag == null) {
               graphTag = doc.createElement("graph");
               Element graphNameTag = doc.createElement("name");
               graphNameTag.appendChild(doc.createTextNode(graphName));
               graphTag.appendChild(graphNameTag);
               rootElement.appendChild(graphTag);
           }

            graphTag.appendChild(newBookmark);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(fileName));
            transformer.transform(source, streamResult);
            System.out.println("Created bookmark " + bookMarkName + " Center Node: " + nodeID
                    + " Radius: " + radius);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException pce) {
            (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be stored.", ButtonType.CLOSE)).show();
        }
    }

    /**
     * Deletes a bookmark from the bookmark file.
     * @param fileName The name of the bookmark file.
     * @param graphName The name of the graph.
     * @param bookmarkName The name of the bookmark to be deleted.
     */
    public static void deleteBookmark(String fileName, String graphName, String bookmarkName) {
        checkFile(fileName);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(fileName);
            doc.getDocumentElement().normalize();
            Element graphTag = findTag(doc.getElementsByTagName("graph"), graphName);

            if (graphTag != null) {
                Element bookmarkTag = findTag(graphTag.getElementsByTagName("bookmark"), bookmarkName);
                if (bookmarkTag != null) {
                    graphTag.removeChild(bookmarkTag);
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult streamResult = new StreamResult(new File(fileName));
                    transformer.transform(source, streamResult);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be deleted.", ButtonType.CLOSE)).show();
        }
    }

    /**
     * Loads all bookmarks present from a particular graph.
     * @param fileName The name of the bookmark file
     * @param graphName The name of the graph
     * @return A list containing all bookmarks for that graph
     */
    public static List<Bookmark> loadAllGraphBookmarks(String fileName, String graphName) {
        List<Bookmark> result = new ArrayList<>();
        checkFile(fileName);
        Document dom;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(fileName);
            dom.getDocumentElement().normalize();
            Element doc = dom.getDocumentElement();
            Element graph = findTag(doc.getElementsByTagName("graph"), graphName);
            if (graph != null) {
                NodeList nodeList = graph.getElementsByTagName("bookmark");
                if (nodeList != null) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element el = (Element) nodeList.item(i);
                            result.add(parseBookmark(el));
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            (new Alert(Alert.AlertType.ERROR, "The bookmarks cannot be loaded.", ButtonType.CLOSE)).show();
        }
        return result;
    }

    /**
     * Find the tag in the xml file belonging to the graph name.
     * @param nodeList is the list of all graph tags
     * @param name is the name of the graph this method tries to find
     * @return The Element containing the graph or null if not found
     */
    private static Element findTag(NodeList nodeList, String name) {
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    if (element.getElementsByTagName("name").item(0).getTextContent().equals(name)) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the given bookmark fileName exists.
     * If not it will create the file with the necessary tags.
     * @param fileName The name of the bookmark file
     */
    private static void checkFile(String fileName) {
        File bookmarkFile = new File(fileName);
        if (!bookmarkFile.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = docFactory.newDocumentBuilder();
                Document doc = builder.newDocument();
                Element rootElement = doc.createElement("graphs");
                doc.appendChild(rootElement);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(bookmarkFile);

                transformer.transform(source, result);
            } catch (ParserConfigurationException | TransformerException e) { }
        }
    }

    /**
     * Parses an xml element to return a bookmark.
     * @param el The xml element containing the bookmark
     * @return A bookmark represented by the element
     */
    private static Bookmark parseBookmark(Element el) {
        if (el != null) {
            return new Bookmark(Integer.parseInt(el.getElementsByTagName("ID").item(0).getTextContent()),
                    Integer.parseInt(el.getElementsByTagName("radius").item(0).getTextContent()),
                    el.getElementsByTagName("name").item(0).getTextContent(),
                    el.getElementsByTagName("description").item(0).getTextContent());
        }
        return null;
    }
}
