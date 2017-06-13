package programminglife.model.drawing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import programminglife.model.GenomeGraph;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A segment that also implements {@link Drawable}.
 */
public interface DrawableNode extends Drawable {

    /**
     * Get the {@link GenomeGraph}.
     * @return the graph
     */
    GenomeGraph getGraph();

    /**
     * Set if the dimensions are up to date.
     * @param upToDate {@link boolean} true if up to date else false
     */
    void setDrawDimensionsUpToDate(boolean upToDate);

    /**
     * Get if the dimensions are up to date.
     * @return boolean true if up to date else false
     */
    boolean isDrawDimensionsUpToDate();

    /**
     * Get the IDs of children of this.
     * @return IDs of drawable children
     */
    Collection<Integer> getChildren();

    /**
     * Get the IDs of parents of this.
     * @return IDs of drawable parents.
     */
    Collection<Integer> getParents();

    /**
     * Replace a parent with another one.
     * @param oldParent the parent to replace
     * @param newParent the new parent
     */
    void replaceParent(DrawableNode oldParent, DrawableNode newParent);

    /**
     * Replace child with another one.
     * @param oldChild the child to replace
     * @param newChild the new child
     */
    void replaceChild(DrawableNode oldChild, DrawableNode newChild);

    /**
     * Information {@link String} about this.
     * @return info
     */
    String details();

    /**
     * Get the IDs of genomes through this.
     * @return the IDs of genomes
     */
    int[] getGenomes();

    /**
     * Color this according to contents.
     */
    void colorize();

    /**
     * Set the location to draw this.
     * @param x the x location
     * @param y the y location
     */
    void setLocation(double x, double y);

    /**
     * Get a {@link Link} from this.
     * @param child the {@link DrawableNode} to get the {@link Link} to
     * @return it's link
     */
    Link getLink(DrawableNode child);

    /**
     * Set the size of this drawing.
     */
    void setDrawDimensions();

    /**
     * Get the width of the node
     * @return The width of the node
     */
    double getWidth();

    /**
     * Get the height of the node
     * @return The height of the node
     */
    double getHeight();

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    XYCoordinate getLeftBorderCenter();

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    XYCoordinate getCenter();

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    XYCoordinate getRightBorderCenter();

    Color getStrokeColor();

    int getIdentifier();

    XYCoordinate getLocation();
}
