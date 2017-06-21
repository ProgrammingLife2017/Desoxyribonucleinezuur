package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A segment that also implements {@link Drawable}.
 */
public abstract class DrawableNode implements Drawable {
    static final double NODE_HEIGHT = 10;

    private final GenomeGraph graph;
    private final int id;
    private final Collection<Integer> genomes;

    private final XYCoordinate location;
    private final XYCoordinate dimensions;
    private boolean drawDimensionsUpToDate = false;

    private double strokeWidth;
    private Color fillColor;
    private Color strokeColor;

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

        int[] genomes = graph.getGenomes(id);
        if (genomes != null) {
            this.genomes = Arrays.stream(genomes).boxed().collect(Collectors.toSet());
        } else {
            this.genomes = Collections.emptySet();
        }
    }

    /**
     * Get the {@link GenomeGraph}.
     * @return the graph
     */
    final GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Set if the dimensions are up to date.
     */
    final void setDrawDimensionsUpToDate() {
        this.drawDimensionsUpToDate = true;
    }

    /**
     * Set the size of this drawing.
     */
    protected abstract void setDrawDimensions();

    /**
     * Get if the dimensions are up to date.
     * @return boolean true if up to date else false
     */
    private boolean isDrawDimensionsUpToDate() {
        return this.drawDimensionsUpToDate;
    }

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
     * Color this according to contents.
     * @param subGraph {@link SubGraph} to colorize.
     */
    public abstract void colorize(SubGraph subGraph);

    /**
     * Set the size {@link XYCoordinate} of the Segment.
     * @param width The double representing the width of the DrawableSegment
     * @param height The double representing the height of the DrawableSegment
     */
    final void setSize(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setDrawDimensionsUpToDate();
    }

    /**
     * Set the location to draw this.
     * @param x the x location
     * @param y the y location
     */
    public final void setLocation(double x, double y) {
        this.location.set(x, y);
    }

    /**
     * Get the width of the node.
     * @return The width of the node.
     */
    public final double getWidth() {
        return dimensions.getX();
    }

    /**
     * Get the height of the node.
     * @return The height of the node.
     */
    public final double getHeight() {
        if (!isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return dimensions.getY();
    }

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    public final XYCoordinate getLeftBorderCenter() {
        if (!isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return new XYCoordinate(location.getX(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    public final XYCoordinate getCenter() {
        if (!isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return new XYCoordinate(location.getX() + 0.5 * getWidth(), location.getY() + 0.5 * getHeight());
    }

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    final XYCoordinate getRightBorderCenter() {
        if (!isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return new XYCoordinate(location.getX() + getWidth(), location.getY() + 0.5 * getHeight());
    }

    public final int getIdentifier() {
        return id;
    }

    public final XYCoordinate getLocation() {
        return location;
    }

    public final Collection<Integer> getGenomes() {
        return this.genomes;
    }

    public final void setWidth(double width) {
        this.dimensions.setX(width);
    }

    public final void setHeight(double height) {
        this.dimensions.setY(height);
    }

    /**
     * Method to add genomes to a Drawable node.
     * @param genomes is a Set of Integers representing the genomeIDs.
     */
    final void addGenomes(Set<Integer> genomes) {
        this.genomes.addAll(genomes);
    }

    /**
     * Return this {@link DrawableNode} if it is a {@link DrawableSegment}, else return its parent.
     * @return the 'closest' parent {@link DrawableSegment}
     */
    public abstract DrawableNode getParentSegment();

    /**
     * Return this {@link DrawableNode} if it is a {@link DrawableSegment}, else return its child.
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
     * @param fillColor {@link Color} is the color to fill the segment with.
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

}
