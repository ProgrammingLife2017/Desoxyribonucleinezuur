package programminglife.model.drawing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import programminglife.model.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

/**
 * A Segment that also Implements {@link Drawable}.
 */
public class DrawableNode extends Rectangle {
    private int nodeID;
    private GenomeGraph graph;

    private boolean drawDimensionsUpToDate = false;
    private Collection<Integer> parents;
    private Collection<Integer> children;


    /**
     * Create a DrawableSegment from a Segment.
     * @param nodeID The segment to create this DrawableSegment from.
     */
    public DrawableNode(int nodeID, GenomeGraph graph) {
        this.nodeID = nodeID;
        this.graph = graph;
        parents = new LinkedHashSet<>();
        children = new LinkedHashSet<>();

        Arrays.stream(graph.getParentIDs(nodeID)).forEach(id -> parents.add(id));
        Arrays.stream(graph.getChildIDs(nodeID)).forEach(id -> children.add(id));

        this.setDrawDimensions();
    }

    /**
     * Get all the children of the node {@link DrawableNode}.
     * @return children {@link Collection} are the direct children of the node {@link DrawableNode}.
     */
    public Collection<Integer> getChildren() {
        return children;
    }

    /**
     * Get all the parents of the node {@link DrawableNode}.
     * @return parent {@link Collection} are the direct parents of the node {@link DrawableNode}.
     **/
    Collection<Integer> getParents() {
        return parents;
    }

    /**
     * Replace a child node with a dummy node.
     * @param oldChild The {@link DrawableNode} to replace.
     * @param newChild The {@link DrawableNode} to replace with.
     */
    void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (!this.children.remove(oldChild.getIdentifier())) {
            throw new NoSuchElementException("The node to be replaced is not a child of this node.");
        }
        this.children.add(newChild.getIdentifier());
    }

    /**
     * Replace a parent node with a dummy node.
     * @param oldParent The {@link DrawableNode} to replace.
     * @param newParent The {@link DrawableNode} to replace with.
     */
    void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (!this.parents.remove(oldParent.getIdentifier())) {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                            oldParent.getIdentifier(), this.getIdentifier()));
        }
        this.parents.add(newParent.getIdentifier());
    }

    public int getIntX() {
        return (int) Math.ceil(this.getX());
    }

    public int getIntY() {
        return (int) Math.ceil(this.getY());
    }

    public int getIntWidth() {
        return (int) Math.ceil(this.getWidth());
    }

    public int getIntHeight() {
        return (int) Math.ceil(this.getHeight());
    }

    /**
     * Get a {@link XYCoordinate} representing the size of the Segment.
     * @return The size of the Segment
     */
    public XYCoordinate getSize() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return new XYCoordinate((int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * Set the size {@link XYCoordinate} of the Segment.
     * @param size The {@link XYCoordinate} representing the size
     */
    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
        this.drawDimensionsUpToDate = true;
    }

    /**
     * Getter for top left corner of a Segment.
     * @return {@link XYCoordinate} with the values of the top left corner.
     */
    private XYCoordinate getLocation() {
        return new XYCoordinate((int) this.getX(), (int) this.getY());
    }

    /**
     * Set an {@link XYCoordinate} representing the location of the Segment.
     * @param location The {@link XYCoordinate}
     */
    void setLocation(XYCoordinate location) {
        this.setX(location.getX());
        this.setY(location.getY());
    }

    /**
     * getter for the width coordinate.
     * @return XYCoordinate.
     */
    public XYCoordinate getWidthCoordinate() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return new XYCoordinate((int) this.getWidth(), 0);
    }

    /**
     * getter for the height coordinate.
     * @return XYCoordinate.
     */
    public XYCoordinate getHeightCoordinate() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return new XYCoordinate(0, (int) this.getHeight());
    }

    /**
     * Setter for the dimension of the node.
     */
    private void setDrawDimensions() {
        if (node instanceof  Dummy) {
            return;
        }

        int segmentLength = this.getSequenceLength();
        int width, height;

        width = 10 + (int) Math.pow(segmentLength, 1.0 / 2);
        height = 10;

        this.setSize(new XYCoordinate(width, height));
        this.drawDimensionsUpToDate = true;
    }

    public double getGenomeFraction() {
        return this.graph.getGenomeFraction(this.nodeID);
    }

    public int[] getGenomes() {
        return this.graph.getGenomes(this.nodeID);
    }



    /**
     * get the length of the sequence of this segment.
     * @return the length of the sequence of this segment
     */
    private int getSequenceLength() {
        return this.graph.getSequenceLength(this.nodeID);
    }

    /**
     * Returns the sequence of all the segments that are part of the DrawableNode.
     * @return A string containing all the sequences appended.
     */
    public String getSequence() {
        return this.graph.getSequence(nodeID);
    }

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    public XYCoordinate getLeftBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getCenter().add(-(this.getSize().getX() / 2), 0);
    }

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    private XYCoordinate getCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getLocation().add(this.getSize().multiply(0.5));
    }

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    public XYCoordinate getRightBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getCenter().add(this.getSize().getX() / 2, 0);
    }

    /**
     * Method to return a string with information about the {@link DrawableNode}.
     * @return a {@link String} representation of a {@link DrawableNode}.
     */
    @Override
    public String toString() {
        return "Segment: "
                + nodeID
                + " "
                + "Location: "
                + this.getLocation().getX()
                + ","
                + this.getLocation().getY();
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof DrawableNode) {
            DrawableNode that = (DrawableNode) other;
            if (that.nodeID == this.nodeID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nodeID;
    }

    /**
     * Get the {@link Link} between this node and the child drawable node.
     * @param child The {@link DrawableNode} that the link goes to.
     * @return {@link Link} between the two nodes.
     */
    Link getLink(DrawableNode child) {
        return graph.getLink(this.nodeID, child.nodeID);
    }

    /**
     * Color a {@link DrawableNode} depending on its properties.
     * @param graph the {@link GenomeGraph} belonging to the {@link DrawableNode}
     */
    public void colorize(GenomeGraph graph) {
        if (this.node instanceof Dummy) {
            DrawableEdge.colorize(this, graph.getLink(nodeID, -1), graph);
        } else {
            double genomeFraction = this.getGenomeFraction();
            double maxSaturation = 0.8, minSaturation = 0.05;
            double saturation = minSaturation + genomeFraction * (maxSaturation - minSaturation);

            Color fillColor = Color.hsb(227, saturation, 1.d);
            Color strokeColor = Color.hsb(227, maxSaturation, 1.d);

            this.setFill(fillColor);
            this.setStroke(strokeColor);
        }
    }

    public int getIdentifier() {
        return nodeID;
    }

    public GenomeGraph getGraph() {
        return this.graph;
    }
}
