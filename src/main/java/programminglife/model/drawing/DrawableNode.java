package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A segment that also implements {@link Drawable}.
 */
public abstract class DrawableNode implements Drawable {

    private final GenomeGraph graph;
    private final int id;

    private Layer layer;
    private XYCoordinate location;
    private XYCoordinate dimensions;

    /**
     * Constructor for a DrawableNode.
     * @param graph The {@link GenomeGraph} that this DrawableNode is part of.
     * @param id The id for this DrawableNode. These should be non-negative for Nodes actually in the
     *           graph ({@link DrawableSegment}), and negative for Nodes that are not in the
     *           graph ({@link DrawableDummy}).
     */
    public DrawableNode(GenomeGraph graph, int id) {
        this.graph = graph;
        this.id = id;

        this.dimensions = new XYCoordinate(0, 0);
        this.location = new XYCoordinate(0, 0);
    }

    /**
     * Get the {@link GenomeGraph}.
     * @return the graph
     */
    GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Set if the dimensions are up to date.
     * @param upToDate {@link boolean} true if up to date else false
     */
    abstract void setDrawDimensionsUpToDate(boolean upToDate);

    /**
     * Get if the dimensions are up to date.
     * @return boolean true if up to date else false
     */
    abstract boolean isDrawDimensionsUpToDate();

    /**
     * Get the IDs of children of this.
     * @return IDs of drawable children
     */
    abstract Collection<Integer> getChildren();

    /**
     * Get the IDs of parents of this.
     * @return IDs of drawable parents.
     */
    abstract Collection<Integer> getParents();

    /**
     * Replace a parent with another one.
     * @param oldParent the parent to replace
     * @param newParent the new parent
     */
    abstract void replaceParent(DrawableNode oldParent, DrawableNode newParent);

    /**
     * Replace child with another one.
     * @param oldChild the child to replace
     * @param newChild the new child
     */
    abstract void replaceChild(DrawableNode oldChild, DrawableNode newChild);

    /**
     * Information {@link String} about this.
     * @return info
     */
    public abstract String details();

    /**
     * Get the IDs of genomes through this.
     * @return the IDs of genomes
     */
    public abstract int[] getGenomes();

    /**
     * Color this according to contents.
     */
    public abstract void colorize();

    /**
     * Set the location to draw this.
     * @param x the x location
     * @param y the y location
     */
    public void setLocation(double x, double y) {
        this.location = new XYCoordinate(x, y);
    }

    /**
     * Get a {@link Link} from this.
     * @param child the {@link DrawableNode} to get the {@link Link} to
     * @return it's link
     */
    public abstract Link getLink(DrawableNode child);

    /**
     * Set the size of this drawing.
     */
    public abstract void updateDrawDimensions();

    /**
     * Get the width of the node.
     * @return The width of the node.
     */
    public double getWidth() {
        return dimensions.getX();
    }

    /**
     * Get the height of the node.
     * @return The height of the node.
     */
    public double getHeight() {
        return dimensions.getY();
    }

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    public XYCoordinate getLeftBorderCenter() {
        return new XYCoordinate(location.getX(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    public XYCoordinate getCenter() {
        return new XYCoordinate(location.getX() + 0.5 * getWidth(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    public XYCoordinate getRightBorderCenter() {
        return new XYCoordinate(location.getX() + getWidth(), location.getY() + 0.5 * getHeight());
    }

    /**
     * Returns the {@link Color} of this DrawableNode.
     * @return the {@link Color} of this DrawableNode.
     */
    public abstract Color getStrokeColor();

    public final int getIdentifier() {
        return id;
    }

    public XYCoordinate getLocation() {
        return location;
    }

    public void setWidth(double width) {
        this.dimensions = new XYCoordinate(width, getHeight());
    }

    public void setHeight(double height) {
        this.dimensions = new XYCoordinate(getWidth(), height);
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }
}
