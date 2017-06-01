package programminglife.model.drawing;

import javafx.scene.shape.Rectangle;
import programminglife.model.*;

import java.util.*;

/**
 * A {@link Segment} that also Implements {@link Drawable}.
 */
public class DrawableNode extends Rectangle {
    private Node node;
    private boolean drawDimensionsUpToDate = false;
    private Collection<Node> parents;
    private Collection<Node> children;


    /**
     * Create a DrawableSegment from a Segment.
     * @param node The segment to create this DrawableSegment from.
     */
    public DrawableNode(Node node) {
        this.node = node;
        parents = new LinkedHashSet<>(node.getParents());
        children = new LinkedHashSet<>(node.getChildren());
        this.setDrawDimensions();
    }


    /**
     * Get all the edges to the children.
     * @return childEdges {@link Collection<DrawableEdge>} are all the edges
     * to the children of the node {@link DrawableNode}.
     */
//    public Collection<DrawableEdge> getChildEdges() {
//        HashSet<DrawableEdge> childEdges = new HashSet<DrawableEdge>();
//
//            for (Edge e: node.getChildEdges()) {
//                childEdges.add(new DrawableEdge(e, this, new DrawableNode(e.getEnd())));
//            }
//
//        return childEdges;
//    }

    /**
     * Get all the edges to the parents.
     * @return parentEdges {@link Collection<DrawableEdge>} are all the edges
     * to the parents of the node {@link DrawableNode}.
     */
    public Collection<DrawableEdge> getParentEdges() {
        HashSet<DrawableEdge> parentEdges = new HashSet<DrawableEdge>();

//            for (Edge e : node.getParentEdges()) {
//                parentEdges.add(new DrawableEdge(e, new DrawableNode(e.getStart()), this));
//            }

        return parentEdges;
    }

    /**
     * Get all the children of the node {@link DrawableNode}.
     * @param foundNodes {@link List<DrawableNode>} is a list with nodes that are already found.
     * @return children {@link Collection<DrawableNode>} are the direct children of the node {@link DrawableNode}.
     */
    public Collection<Node> getChildren() {
        return children;
    }

    /**
     * Get all the parents of the node {@link DrawableNode}.
     * @param foundNodes {@link List<DrawableNode>} is a list with nodes that are already found.
     * @return parent {@link Collection<DrawableNode>} are the direct parents of the node {@link DrawableNode}.
     **/
    public Collection<Node> getParents() {
        return parents;
    }

    public void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (!this.children.remove(oldChild.getNode())) {
            throw new NoSuchElementException("The node to be replaced is not a child of this node.");
        }
        this.children.add(newChild.getNode());
    }

    public void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (!this.parents.remove(oldParent.getNode())) {
            throw new NoSuchElementException("The node to be replaced is not a parent of this node.");
        }
        this.parents.add(newParent.getNode());
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
     * Get a {@link XYCoordinate} representing the size of the {@link Segment}.
     * @return The size of the {@link Segment}
     */
    public XYCoordinate getSize() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return new XYCoordinate((int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * Set the size {@link XYCoordinate} of the {@link Segment}.
     * @param size The {@link XYCoordinate} representing the size
     */
    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
        this.drawDimensionsUpToDate = true;
    }

    /**
     * Getter for top left corner of a {@link Segment}.
     * @return {@link XYCoordinate} with the values of the top left corner.
     */
    public XYCoordinate getLocation() {
        return new XYCoordinate((int) this.getX(), (int) this.getY());
    }

    /**
     * Set an {@link XYCoordinate} representing the location of the {@link Segment}.
     * @param location The {@link XYCoordinate}
     */
    public void setLocation(XYCoordinate location) {
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

    /**
     * get the length of the sequence of this segment.
     * @return the length of the sequence of this segment
     */
    public int getSequenceLength() {
        return node.getSequenceLength();
    }

    /**
     * Returns the sequence of all the segments that are part of the DrawableNode.
     * @return A string containing all the sequences appended.
     */
    public String getSequence() {
        return DataManager.getSequence(node.getIdentifier());
    }

    public Node getNode() {
        return this.node;
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
    public XYCoordinate getCenter() {
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
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Segments: ");
        stringBuilder.append(node.getIdentifier());
        stringBuilder.append(" ");
        stringBuilder.append("Location: ");
        stringBuilder.append(this.getLocation().getX());
        stringBuilder.append(",");
        stringBuilder.append(this.getLocation().getY());


        return stringBuilder.toString();
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof DrawableNode) {
            DrawableNode that = (DrawableNode) other;
            if (that.node.equals(this.node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return node.getIdentifier();
    }

    public Link getLink(DrawableNode child) {
        return node.getLink(child.getNode());
    }
}
