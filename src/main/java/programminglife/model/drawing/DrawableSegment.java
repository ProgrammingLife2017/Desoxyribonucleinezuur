package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Segment that also Implements {@link Drawable}.
 */
public class DrawableSegment extends DrawableNode {
    private Set<Integer> parents;
    private Set<Integer> children;

    /**
     * Create a DrawableSegment from a Segment.
     * @param graph the graph this Segment is in
     * @param nodeID The segment to create this DrawableSegment from.
     */
    public DrawableSegment(GenomeGraph graph, int nodeID) {
        super(graph, nodeID);

        parents = Arrays.stream(graph.getParentIDs(nodeID)).boxed().collect(Collectors.toSet());
        children = Arrays.stream(graph.getChildIDs(nodeID)).boxed().collect(Collectors.toSet());
        this.addGenomes(Arrays.stream(graph.getGenomes(nodeID)).boxed().collect(Collectors.toSet()));

        this.setDrawDimensions();
    }

    /**
     * Get all the children of the node {@link DrawableSegment}.
     * @return children {@link Collection} are the direct children of the node {@link DrawableSegment}.
     */
    @Override
    public Set<Integer> getChildren() {
        return children;
    }

    /**
     * Get all the parents of the node {@link DrawableSegment}.
     * @return parent {@link Collection} are the direct parents of the node {@link DrawableSegment}.
     **/
    public Set<Integer> getParents() {
        return parents;
    }

    /**
     * Replace a child node with a dummy node.
     * @param oldChild The {@link DrawableSegment} to replace.
     * @param newChild The {@link DrawableSegment} to replace with.
     */
    void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (!this.children.remove(oldChild.getIdentifier())) {
            throw new NoSuchElementException("The node to be replaced is not a child of this node.");
        }
        this.children.add(newChild.getIdentifier());
    }

    @Override
    public String details() {
        return String.format("Sequence: %s%nGenomes: %s",
                this.getSequence(), this.getGraph().getGenomeNames(this.getGenomes()));
    }

    /**
     * Replace a parent node with a dummy node.
     * @param oldParent The {@link DrawableSegment} to replace.
     * @param newParent The {@link DrawableSegment} to replace with.
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
     * Set the size {@link XYCoordinate} of the Segment.
     * @param size The {@link XYCoordinate} representing the size
     */
    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
        this.setDrawDimensionsUpToDate(true);
    }

    /**
     * Set the location of the Segment.
     * @param x the x location
     * @param y the y location
     */
    void setLocation(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * getter for the width coordinate.
     * @return XYCoordinate.
     */
    public XYCoordinate getWidthCoordinate() {
        if (!this.isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return new XYCoordinate((int) this.getWidth(), 0);
    }

    /**
     * getter for the height coordinate.
     * @return XYCoordinate.
     */
    public XYCoordinate getHeightCoordinate() {
        if (!this.isDrawDimensionsUpToDate()) {
            setDrawDimensions();
        }
        return new XYCoordinate(0, (int) this.getHeight());
    }

    /**
     * Setter for the dimension of the node.
     */
    protected void setDrawDimensions() {
        int segmentLength = this.getSequenceLength();
        int width, height;

        width = 10 + (int) Math.pow(segmentLength, 1.0 / 2);
        height = NODE_HEIGHT;

        this.setSize(new XYCoordinate(width, height));
        this.setDrawDimensionsUpToDate(true);
    }

    @Override
    public DrawableNode getParentSegment() {
        return this; // Don't ask!
    }

    @Override
    public DrawableNode getChildSegment() {
        return this; // Don't ask!
    }

    @Override
    public DrawableSegment hasSNPChildren(SubGraph subGraph) {
        Collection<DrawableNode> childChildren;

        // A SNP-able node has:
        if (children.size() < 2) { // - at least 2 children
            return null;
        } else if (children.size() > 4) { // - at most 4 children
            return null;
        } else if (children.stream().anyMatch(id -> id < 1)) { // - no dummy children
            return null;
        } else if (children.stream().anyMatch(id -> getGraph().getSequenceLength(id) != 1)) { // - only children of length 1
            return null;
        } else  {
            Collection<DrawableNode> childNodes = subGraph.getChildren(this);
            Collection<DrawableNode> childParents = childNodes.stream()
                    .map(subGraph::getParents)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            childChildren = childNodes.stream()
                    .map(subGraph::getChildren)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            if (childChildren.size() != 1) { // - all children have 1 and the same child
                return null;
            } else if (childParents.size() != 1) { // - all children have 1 and the same parent
                return null;
            }
        }
        return (DrawableSegment) childChildren.iterator().next();
    }

    public double getGenomeFraction() {
        return this.getGraph().getGenomeFraction(this.getIdentifier());
    }

    /**
     * get the length of the sequence of this segment.
     * @return the length of the sequence of this segment
     */
    private int getSequenceLength() {
        return this.getGraph().getSequenceLength(this.getIdentifier());
    }

    /**
     * Returns the sequence of all the segments that are part of the DrawableNode.
     * @return A string containing all the sequences appended.
     */
    public String getSequence() {
        return this.getGraph().getSequence(getIdentifier());
    }

    /**
     * Method to return a string with information about the {@link DrawableSegment}.
     * @return a {@link String} representation of a {@link DrawableSegment}.
     */
    @Override
    public String toString() {
        return "Segment: "
                + getIdentifier()
                + " "
                + "Location: "
                + this.getLocation().getX()
                + ","
                + this.getLocation().getY();
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof DrawableSegment) {
            DrawableSegment that = (DrawableSegment) other;
            if (that.getIdentifier() == this.getIdentifier()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getIdentifier();
    }

    /**
     * Color a {@link DrawableSegment} depending on its properties.
     */
    @Override
    public void colorize(SubGraph sg) {
        double genomeFraction = this.getGenomeFraction();
        double maxSaturation = 0.8, minSaturation = 0.05;
        double saturation = minSaturation + genomeFraction * (maxSaturation - minSaturation);

        Color fillColor = Color.hsb(227, saturation, 1.d);
        Color strokeColor = Color.hsb(227, maxSaturation, 1.d);

        this.setFill(fillColor);
        this.setStroke(strokeColor);
    }
}
