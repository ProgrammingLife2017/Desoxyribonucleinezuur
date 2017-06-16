package programminglife.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a bookmark. Consists of a location and radius.
 */
public class Bookmark {
    private String graphName;
    private String path;
    private int radius;
    private int nodeID;
    private String bookmarkName;
    private String description;

    /**
     * Initialize a bookmark.
     * @param graphName The graph file where this bookmark belongs to
     * @param path The path where the graph file is located.
     * @param bookmarkName this is the bookmarkName of the file in which this genome and location is present
     * @param nodeID is the ID of the node where the bookmark is present.
     * @param radius is the depth to which surrounding nodes will be visualized.
     * @param description The text describing this bookmark.
     */
    public Bookmark(String graphName, String path, int nodeID, int radius, String bookmarkName, String description) {
        this.graphName = graphName;
        this.path = path;
        this.radius = radius;
        this.nodeID = nodeID;
        this.bookmarkName = bookmarkName;
        this.description = description;
    }

    public StringProperty getNameProperty() {
        return new SimpleStringProperty(this.bookmarkName);
    }

    public StringProperty getDescriptionProperty() {
        return new SimpleStringProperty(this.description);
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String file) {
        this.graphName = file;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Bookmark) {
            Bookmark that = (Bookmark) other;
            if (this.graphName.equals(that.getGraphName())
                    && this.radius == that.getRadius()
                    && this.nodeID == that.getNodeID()
                    && this.bookmarkName.equals(that.getBookmarkName())
                    && this.description.equals(that.getDescription())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ("{file: " + this.graphName + ", name: " + this.bookmarkName
                + ", description: " + this.description
                + ", ID: " + this.nodeID
                + ", radius: " + this.radius + "}");
    }
}
