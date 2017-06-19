package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import java.util.*;

/**
 * A class that handles the creation and usage of dummy nodes.
 */
public class DrawableDummy extends DrawableNode {
    public static final int DUMMY_HEIGHT = 5;

    private Color strokeColor;
    private double strokeWidth;
    private XYCoordinate location;
    private double width;
    private double height;
    private boolean drawDimensionsUpToDate = false;

    private DrawableNode parent;
    private DrawableNode child;

    /**
     * Create a dummy node.
     * @param id the ID
     * @param parentNode the dummy's parent
     * @param childNode the dummy's child
     * @param graph the GenomeGraph currently drawn
     */
    public DrawableDummy(int id, DrawableNode parentNode, DrawableNode childNode, GenomeGraph graph) {
        super(graph, id);

        this.parent = parentNode;
        this.child = childNode;
    }

    @Override
    public Collection<Integer> getChildren() {
        Collection<Integer> result = new LinkedHashSet();
        result.add(child.getIdentifier());
        return result;
    }

    @Override
    public Collection<Integer> getParents() {
        Collection result = new LinkedHashSet();
        result.add(parent.getIdentifier());
        return result;
    }

    @Override
    public void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parent.getIdentifier() == oldParent.getIdentifier()) {
            this.parent = newParent;
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                    oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    public void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (this.child.getIdentifier() == oldChild.getIdentifier()) {
            this.child = newChild;

        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a child of this node (%d).",
                            oldChild.getIdentifier(), this.getIdentifier()));
        }
    }

    /**
     * Information {@link String} about this.
     *
     * @return info
     */
    @Override
    public String details() {
        return toString();
    }

    @Override
    public void colorize(SubGraph sg) {
        double genomeFraction = 0.d;
        Map<DrawableNode, Collection<Integer>> from = sg.getGenomes().get(this.getParentSegment());
        if (from != null) {
            Collection<Integer> genomes = from.get(this.getChildSegment());
            if (genomes != null) {
                genomeFraction = genomes.size() / (double) sg.getNumberOfGenomes();
            }
        }

        double minStrokeWidth = 1.d, maxStrokeWidth = 6.5;
        double strokeWidth = minStrokeWidth + genomeFraction * (maxStrokeWidth - minStrokeWidth);

        double minBrightness = 0.6, maxBrightness = 0.25;
        double brightness = minBrightness + genomeFraction * (maxBrightness - minBrightness);

        Color strokeColor = Color.hsb(0.d, 0.d, brightness);

        this.setStrokeWidth(strokeWidth);
        this.setStrokeColor(strokeColor);
    }

    @Override
    public void setLocation(double x, double y) {
        this.location = new XYCoordinate(x, y);
    }

    @Override
        protected void setDrawDimensions() {

    }

    @Override
    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    @Override
    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public void setDrawDimensionsUpToDate(boolean upToDate) {
        this.drawDimensionsUpToDate = upToDate;
    }

    @Override
    public boolean isDrawDimensionsUpToDate() {
        return this.drawDimensionsUpToDate;
    }

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    public XYCoordinate getLeftBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getLocation().add(0, -(this.getHeight() / 2));
    }

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    public XYCoordinate getCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getLocation().add(this.width * 0.5, this.height * 0.5);
    }

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    public XYCoordinate getRightBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getCenter().add(this.width / 2, 0);
    }

    @Override
    public Color getStrokeColor() {
        return this.strokeColor;
    }

    @Override
    public XYCoordinate getLocation() {
        return this.location;
    }

    public String toString() {
        return String.format("Link from %s to %s", this.parent, this.child);
    }

    @Override
    public DrawableNode getParentSegment() {
        return this.parent.getParentSegment();
    }

    @Override
    public DrawableNode getChildSegment() {
        return this.child.getChildSegment();
    }
}
