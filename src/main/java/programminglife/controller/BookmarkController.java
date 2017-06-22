package programminglife.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import programminglife.model.Bookmark;
import programminglife.utility.Alerts;
import programminglife.utility.Console;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
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
     * Loads all the bookmarks and puts them in a Map.
     * @return Map containing the bookmarks with their keys.
     */
    public static Map<String, List<Bookmark>> loadAllBookmarks() {
        return loadAllBookmarks(BOOKMARKPATH);
    }

    /**
     * Default bookmark storing method.
     * Uses the default bookmark path.
     * @param bookmark The bookmark to store.
     * @return true if the bookmark is stored and did not exist yet, false otherwise
     */
    public static boolean storeBookmark(Bookmark bookmark) {
        return storeBookmark(BOOKMARKPATH, bookmark);
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
        Element doc = loadDoc(fileName);
        assert doc != null;
        Element graph = findTag(doc.getElementsByTagName("graph"), graphName);
        if (graph != null) {
            Element bookmark = findTag(graph.getElementsByTagName("bookmark"), bookmarkName);
            if (bookmark != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Store a bookmark in bookmarks.xml.
     * @param fileName The path of the bookmarks file.
     * @param bookmark The bookmark to store.
     * @return true if the bookmark already exists, false otherwise.
     */
    static boolean storeBookmark(String fileName, Bookmark bookmark) {
        checkFile(fileName);
        if (bookmarkExists(fileName, bookmark.getGraphName(), bookmark.getBookmarkName())) {
            return false;
        }
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(fileName);
            doc.getDocumentElement().normalize();
            Element rootElement = doc.getDocumentElement();
            Element newBookmark = createNewBookmarkTag(doc, bookmark);
            Element graphTag = findTag(doc.getElementsByTagName("graph"), bookmark.getGraphName());

            // No earlier bookmarks in this graph
           if (graphTag == null) {
               graphTag = doc.createElement("graph");
               Element graphNameTag = doc.createElement("name");
               graphNameTag.appendChild(doc.createTextNode(bookmark.getGraphName()));
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
            Console.println("Created bookmark " + bookmark.getBookmarkName() + " Center Node: " + bookmark.getNodeID()
                    + " Radius: " + bookmark.getRadius());
            return true;
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException pce) {
            Alerts.error("The bookmarks cannot be stored");
        }
        return false;
    }

    /**
     * Creates a bookmark Element.
     * @param doc The Document to create the bookmark with
     * @param bookmark The bookmark to create the tag for
     * @return An element containing the new bookmark
     */
    private static Element createNewBookmarkTag(Document doc, Bookmark bookmark) {
        Element newBookmark = doc.createElement("bookmark");
        Element nameTag = doc.createElement("name");
        nameTag.appendChild(doc.createTextNode(bookmark.getBookmarkName()));
        Element descriptionTag = doc.createElement("description");
        descriptionTag.appendChild(doc.createTextNode(bookmark.getDescription()));
        Element radiusTag = doc.createElement("radius");
        radiusTag.appendChild(doc.createTextNode(String.valueOf(bookmark.getRadius())));
        Element iDTag = doc.createElement("ID");
        iDTag.appendChild(doc.createTextNode(String.valueOf(bookmark.getNodeID())));
        Element pathTag = doc.createElement("path");
        pathTag.appendChild(doc.createTextNode(bookmark.getPath()));

        newBookmark.appendChild(nameTag);
        newBookmark.appendChild(iDTag);
        newBookmark.appendChild(radiusTag);
        newBookmark.appendChild(descriptionTag);
        newBookmark.appendChild(pathTag);
        return newBookmark;
    }

    /**
     * Deletes a bookmark from the bookmark file.
     * @param fileName The name of the bookmark file.
     * @param graphName The name of the graph.
     * @param bookmarkName The name of the bookmark to be deleted.
     */
    static void deleteBookmark(String fileName, String graphName, String bookmarkName) {
        Element doc = loadDoc(fileName);
        assert doc != null;
        Element graphTag = findTag(doc.getElementsByTagName("graph"), graphName);
        if (graphTag != null) {
            Element bookmarkTag = findTag(graphTag.getElementsByTagName("bookmark"), bookmarkName);
            if (bookmarkTag != null) {
                graphTag.removeChild(bookmarkTag);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer;
                try {
                    transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(doc);
                    StreamResult streamResult = new StreamResult(new File(fileName));
                    transformer.transform(source, streamResult);
                } catch (TransformerException e) {
                    Alerts.error("The bookmarks cannot be deleted");
                }
            }
        }
    }

    /**
     * Finds and loads the doc element from the bookmark file.
     * @param fileName The name of the file where the bookmarks reside.
     * @return A DOM Element containing all graphs and bookmarks.
     */
    static Element loadDoc(String fileName) {
        checkFile(fileName);
        Document dom;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(fileName);
            dom.getDocumentElement().normalize();
            return dom.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Alerts.error("Bookmark file error");
        }
        return null;
    }

    /**
     * Load all bookmarks from all files.
     * @param fileName The bookmark file from which to read
     * @return A map of lists containing all bookmarks
     */
    static Map<String, List<Bookmark>> loadAllBookmarks(String fileName) {
        Map<String, List<Bookmark>> result = new HashMap<>();
        Element doc = loadDoc(fileName);
        assert doc != null;
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
     * @param bookmarks The nodeList containing all bookmarks
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
                    el.getElementsByTagName("path").item(0).getTextContent(),
                    Integer.parseInt(el.getElementsByTagName("ID").item(0).getTextContent()),
                    Integer.parseInt(el.getElementsByTagName("radius").item(0).getTextContent()),
                    el.getElementsByTagName("name").item(0).getTextContent(),
                    el.getElementsByTagName("description").item(0).getTextContent());
        }
        return null;
    }
}
