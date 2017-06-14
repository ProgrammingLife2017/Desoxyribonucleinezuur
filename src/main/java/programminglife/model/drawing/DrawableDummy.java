package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

/**
 * A class that handles the creation and usage of dummy nodes.
 */
public class DrawableDummy implements DrawableNode {
    public static final int DUMMY_HEIGHT = 5;

    private final GenomeGraph graph;
    private final int id;

    private Color strokeColor;
    private double strokeWidth;
    private XYCoordinate location;
    private double width;
    private double height;
    private boolean drawDimensionsUpToDate = false;

    private int parentID;
    private int childID;
    private Link link;

    /**
     * Create a dummy node.
     * @param id the ID
     * @param parentNode the dummy's parent
     * @param childNode the dummy's child
     * @param graph the GenomeGraph currently drawn
     */
    public DrawableDummy(int id, DrawableNode parentNode, DrawableNode childNode, GenomeGraph graph) {
        this.graph = graph;
        this.id = id;

        this.parentID = parentNode.getIdentifier();
        this.childID = childNode.getIdentifier();
        this.link = parentNode.getLink(childNode);
    }

    @Override
    public Collection<Integer> getChildren() {
        Collection<Integer> result = new LinkedHashSet();
        result.add(childID);
        return result;
    }

    @Override
    public Collection<Integer> getParents() {
        Collection result = new LinkedHashSet();
        result.add(parentID);
        return result;
    }

    @Override
    public void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parentID == oldParent.getIdentifier()) {
            this.parentID = newParent.getIdentifier();
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                    oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    public void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (this.childID == oldChild.getIdentifier()) {
            this.childID = newChild.getIdentifier();
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a child of this node (%d).",
                            oldChild.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    public String details() {
        return this.link.details();
    }

    @Override
    public int[] getGenomes() {
        return this.link.getGenomes();
    }

    @Override
    public void colorize() {
        DrawableEdge.colorize(this, this.link, this.getGraph());
    }

    @Override
    public void setLocation(double x, double y) {
        this.location = new XYCoordinate(x, y);
    }

    @Override
    public Link getLink(DrawableNode child) {
        return this.link;
    }

    @Override
    public void setDrawDimensions() { }

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
    public GenomeGraph getGraph() {
        return this.graph;
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
    public int getIdentifier() {
        return this.id;
    }

    @Override
    public XYCoordinate getLocation() {
        return this.location;
    }
}
