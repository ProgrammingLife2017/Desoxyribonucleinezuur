package programminglife.model;

/**
 * Created by Martijn van Meerten and Iwan Hoogenboom on 15-5-2017.
 * Class representing a bookmark. Consists of a location and radius.
 */
public class BookMark {
    private int radius;
    private int nodeID;
    private String bookmarkName;
    private String description;
    private XYCoordinate coordinate;
    private double zoom;

    /**
     * Initialize a bookmark.
     * @param bookmarkName this is the bookmarkName of the file in which this genome and location is present
     * @param nodeID is the ID of the node where the bookmark is present.
     * @param radius is the depth to which surrounding nodes will be visualized.
     */
    public BookMark(String bookmarkName, String description, int nodeID, int radius, XYCoordinate coordinate, double zoom) {
        this.radius = radius;
        this.nodeID = nodeID;
        this.bookmarkName = bookmarkName;
        this.description = description;
        this.coordinate = coordinate;
        this.zoom = zoom;

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
}
