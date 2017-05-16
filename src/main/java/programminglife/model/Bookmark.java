package programminglife.model;

/**
 * Created by Martijn van Meerten and Iwan Hoogenboom on 15-5-2017.
 * Class representing a bookmark. Consists of a location and radius.
 */
public class Bookmark {
    private int radius;
    private int nodeID;
    private String bookmarkName;
    private String description;

    /**
     * Initialize a bookmark.
     * @param bookmarkName this is the bookmarkName of the file in which this genome and location is present
     * @param nodeID is the ID of the node where the bookmark is present.
     * @param radius is the depth to which surrounding nodes will be visualized.
     */
    public Bookmark(int nodeID, int radius, String bookmarkName, String description) {
        this.radius = radius;
        this.nodeID = nodeID;
        this.bookmarkName = bookmarkName;
        this.description = description;
    }

    public String getbookmarkName() {
        return bookmarkName;
    }

    public void setbookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public boolean equals(Object other) {
        if (other instanceof Bookmark) {
            Bookmark that = (Bookmark) other;
            if (this.radius == that.getRadius()
                    && this.nodeID == that.getNodeID()
                    && this.bookmarkName.equals(that.getbookmarkName())
                    && this.description.equals(that.getDescription())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ("{name: " + this.bookmarkName
                + ", description: " + this.description
                + ", ID " + this.nodeID
                + ", radius: " + this.radius + "}");
    }
}
