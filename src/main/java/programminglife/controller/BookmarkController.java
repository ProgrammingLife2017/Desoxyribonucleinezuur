package programminglife.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import programminglife.model.Bookmark;
import programminglife.utility.Alerts;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
     * Default all bookmark loading method.
     * Uses the default bookmark path
     * @param graphName The name of the graph from which to get the bookmarks.
     * @return A List containing all bookmarks.
     */
    public static List<Bookmark> loadAllGraphBookmarks(String graphName) {
        return loadAllGraphBookmarks(BOOKMARKPATH, graphName);
    }

    /**
     * Loads all the bookmarks and puts them in a Map.
     * @return Map containing the bookmarks with their keys.
     */
    public static Map<String, List<Bookmark>> loadAllBookmarks() {
        return loadAllBookmarks(BOOKMARKPATH);
    }

    /**
     * Default bookmark storing method.
     * Uses the default bookmark path.
     * @param graphName The name of the graph file.
     * @param bookMarkName The name of the bookmark (must be unique for the graph).
     * @param description The description of this bookmark.
     * @param nodeID The ID of the node for searching.
     * @param radius The radius of the bookmark for searching.
     * @return true if the bookmark is stored and did not exist yet, false otherwise
     */
    public static boolean storeBookmark(String graphName, String bookMarkName,
                                     String description, int nodeID, int radius) {
        return storeBookmark(BOOKMARKPATH, graphName, bookMarkName, description, nodeID, radius);
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
     * Checks whether a bookmark exists.
     * @param fileName The name of the bookmarks file
     * @param graphName The name of the graph file
     * @param bookmarkName The name of the bookmark
     * @return true if it exists, false otherwise
     */
    private static boolean bookmarkExists(String fileName, String graphName, String bookmarkName) {
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
                Element bookmark = findTag(graph.getElementsByTagName("bookmark"), bookmarkName);
                if (bookmark != null) {
                    return true;
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alerts.error("Bookmark file error").show();
        }
        return false;
    }

    /**
     * Store a bookmark in bookmarks.xml.
     * @param fileName The path of the bookmarks file.
     * @param graphName The name of the graph file.
     * @param bookMarkName The name of the bookmark (must be unique for the graph.
     * @param description The description of this bookmark.
     * @param nodeID The ID of the node for searching.
     * @param radius The radius of the bookmark for searching.
     * @return true if the bookmark already exists, false otherwise.
     */
    public static boolean storeBookmark(String fileName, String graphName, String bookMarkName,
                                     String description, int nodeID, int radius) {
        checkFile(fileName);
        if (bookmarkExists(fileName, graphName, bookMarkName)) {
            return false;
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
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(fileName));
            transformer.transform(source, streamResult);
            System.out.println("Created bookmark " + bookMarkName + " Center Node: " + nodeID
                    + " Radius: " + radius);
            return true;
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException pce) {
            (new Alert(Alert.AlertType.ERROR, "This bookmark cannot be stored.", ButtonType.CLOSE)).show();
        }
        return false;
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
                result.addAll(parseBookmarks(graphName, nodeList));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            (new Alert(Alert.AlertType.ERROR, "The bookmarks cannot be loaded.", ButtonType.CLOSE)).show();
        }
        return result;
    }

    /**
     * Load all bookmarks from all files.
     * @param fileName The bookmark file from which to read
     * @return A map of lists containing all bookmarks
     */
    public static Map<String, List<Bookmark>> loadAllBookmarks(String fileName) {
        Map<String, List<Bookmark>> result = new HashMap<>();
        checkFile(fileName);
        Document dom;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(fileName);
            dom.getDocumentElement().normalize();
            Element doc = dom.getDocumentElement();
            NodeList graphs = doc.getElementsByTagName("graph");
            if (graphs != null) {
                for (int i = 0; i < graphs.getLength(); i++) {
                    if (graphs.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) graphs.item(i);
                        String graphName = element.getElementsByTagName("name").item(0).getTextContent();
                        NodeList bookmarks = element.getElementsByTagName("bookmark");
                        result.put(graphName, parseBookmarks(graphName, bookmarks));
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
            } catch (ParserConfigurationException | TransformerException e) {
                Alerts.error("The file could not be found or created");
            }
        }
    }

    /**
     * Parses a list of bookmark nodes and returns them.
     * @param graphName The file these bookmarks are about.
     * @param bookmarks The nodelist containing all bookmarks
     * @return A bookmark represented by the element
     */
    private static List<Bookmark> parseBookmarks(String graphName, NodeList bookmarks) {
        List<Bookmark> result = new ArrayList<>();
        if (bookmarks != null) {
            for (int j = 0; j < bookmarks.getLength(); j++) {
                if (bookmarks.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) bookmarks.item(j);
                    result.add(parseBookmark(graphName, el));
                }
            }
            return result;
        }
        return null;
    }

    /**
     * Parses an xml element to return a bookmark.
     * @param graphName The file that this bookmark is about
     * @param el The element containing the bookmark
     * @return The bookmark from the element.
     */
    private static Bookmark parseBookmark(String graphName, Element el) {
        if (el != null) {
            return new Bookmark(graphName,
                    Integer.parseInt(el.getElementsByTagName("ID").item(0).getTextContent()),
                    Integer.parseInt(el.getElementsByTagName("radius").item(0).getTextContent()),
                    el.getElementsByTagName("name").item(0).getTextContent(),
                    el.getElementsByTagName("description").item(0).getTextContent());
        }
        return null;
    }
}
