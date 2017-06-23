package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A segment that also implements {@link Drawable}.
 */
public abstract class DrawableNode implements Drawable {
    static final double NODE_HEIGHT = 10;
    private static int uniqueId = 0;

    private final GenomeGraph graph;
    private final int id;
    private Layer layer;

    private final XYCoordinate location;
    private final XYCoordinate dimensions;

    private double strokeWidth;
    private Color fillColor;
    private Color strokeColor;

    /**
     * Constructor for a DrawableNode.
     *
     * @param graph The {@link GenomeGraph} that this DrawableNode is part of.
     * @param id    The id for this DrawableNode. These should be non-negative for Nodes actually in the
     *              graph ({@link DrawableSegment}), and negative for Nodes that are not in the
     *              graph ({@link DrawableDummy}).
     */
    DrawableNode(GenomeGraph graph, int id) {
        this.graph = graph;
        this.id = id;

        this.dimensions = new XYCoordinate(0, 0);
        this.location = new XYCoordinate(0, 0);
    }

    @Override
    public final boolean equals(Object o) {
        return this == o || this.getClass().equals(o.getClass())
                && this.getIdentifier() == ((DrawableNode) o).getIdentifier();

    }

    @Override
    public final int hashCode() {
        int result = getGraph().hashCode();
        result = 31 * result + getIdentifier();
        return result;
    }

    /**
     * Get the ID.
     *
     * @return the ID
     */
    public final int getIdentifier() {
        return this.id;
    }

    /**
     * Get the {@link GenomeGraph}.
     *
     * @return the graph
     */
    final GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Get the IDs of children of this.
     *
     * @return IDs of drawable children
     */
    abstract Collection<Integer> getChildren();

    /**
     * Get the IDs of parents of this.
     *
     * @return IDs of drawable parents.
     */
    abstract Collection<Integer> getParents();

    /**
     * Replace a parent with another one.
     *
     * @param oldParent the parent to replace
     * @param newParent the new parent
     */
    abstract void replaceParent(DrawableNode oldParent, DrawableNode newParent);

    /**
     * Replace child with another one.
     *
     * @param oldChild the child to replace
     * @param newChild the new child
     */
    abstract void replaceChild(DrawableNode oldChild, DrawableNode newChild);

    /**
     * Information {@link String} about this.
     *
     * @return info
     */
    public abstract String details();

    /**
     * Checks if the children of this {@link DrawableNode} can be merged as a SNP.
     *
     * @param subGraph the {@link SubGraph} this {@link DrawableNode} is in
     * @return null if children cannot be SNP'ed, SNP with (parent, child and mutation) otherwise
     */
    public abstract DrawableSNP createSNPIfPossible(SubGraph subGraph);

    /**
     * Color this according to contents.
     *
     * @param subGraph  {@link SubGraph} to colorize.
     * @param zoomLevel is double with a value which describes what the current zoom is.
     */
    public abstract void colorize(SubGraph subGraph, double zoomLevel);

    /**
     * Set the size {@link XYCoordinate} of the Segment.
     *
     * @param width  The double representing the width of the DrawableSegment
     * @param height The double representing the height of the DrawableSegment
     */
    final void setSize(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * Set the location to draw this.
     *
     * @param x the x location
     * @param y the y location
     */
    public final void setLocation(double x, double y) {
        this.location.set(x, y);
    }

    /**
     * Get the width of the node.
     *
     * @return The width of the node.
     */
    public final double getWidth() {
        return dimensions.getX();
    }

    /**
     * Get the height of the node.
     *
     * @return The height of the node.
     */
    public final double getHeight() {
        return dimensions.getY();
    }

    /**
     * getter for the center of the left border.
     *
     * @return XYCoordinate.
     */
    public final XYCoordinate getLeftBorderCenter() {
        return new XYCoordinate(location.getX(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center.
     *
     * @return XYCoordinate.
     */
    public final XYCoordinate getCenter() {
        return new XYCoordinate(location.getX() + 0.5 * getWidth(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center of the right border.
     *
     * @return XYCoordinate.
     */
    final XYCoordinate getRightBorderCenter() {
        return new XYCoordinate(location.getX() + getWidth(), location.getY() + 0.5 * getHeight());
    }

    public final XYCoordinate getLocation() {
        return location;
    }

    public abstract Collection<Integer> getGenomes();

    public final void setWidth(double width) {
        this.dimensions.setX(width);
    }

    public final void setHeight(double height) {
        this.dimensions.setY(height);
    }

    /**
     * Return this {@link DrawableNode} if it is a {@link DrawableSegment}, else return its parent.
     *
     * @return the 'closest' parent {@link DrawableSegment}
     */
    public abstract DrawableNode getParentSegment();

    /**
     * Return this {@link DrawableNode} if it is a {@link DrawableSegment}, else return its child.
     *
     * @return the 'closest' child {@link DrawableSegment}
     */
    public abstract DrawableNode getChildSegment();

    public final void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public final void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * Method to set the fill and stroke color of a {@link DrawableSegment}.
     *
     * @param fillColor   {@link Color} is the color to fill the segment with.
     * @param strokeColor {@link Color} is the color of the stroke.
     */
    final void setColors(Color fillColor, Color strokeColor) {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
    }

    public final Color getStrokeColor() {
        return this.strokeColor;
    }

    public final Color getFillColor() {
        return this.fillColor;
    }

    public final double getStrokeWidth() {
        return this.strokeWidth;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    /**
     * Get a unique ID for a DrawableNode. These IDs are always negative. These IDs are globally unique,
     * which means that every call to this method will return a different number (until you reach underflow,
     * which is assumed to not happen)
     *
     * @return A negative unique ID.
     */
    public static synchronized int getUniqueId() {
        uniqueId--;
        return uniqueId;
    }

    public abstract Collection<Integer> getParentGenomes();

    protected abstract Collection<Integer> getChildGenomes();
}
