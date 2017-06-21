package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Segment that also Implements {@link Drawable}.
 */
public class DrawableSegment extends DrawableNode {
    private static final double DRAWABLE_SEGMENT_STROKE_WIDTH = 3.0;

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
    @Override
    public Set<Integer> getParents() {
        return parents;
    }

    /**
     * Replace a child node with a dummy node.
     * @param oldChild The {@link DrawableSegment} to replace.
     * @param newChild The {@link DrawableSegment} to replace with.
     */
    @Override
    public void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
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
    @Override
    public void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (!this.parents.remove(oldParent.getIdentifier())) {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                            oldParent.getIdentifier(), this.getIdentifier()));
        }
        this.parents.add(newParent.getIdentifier());
    }

    /**
     * Setter for the dimension of the node.
     */
    @Override
    public void setDrawDimensions() {
        int segmentLength = this.getSequenceLength();
        double width, height;

        width = 10 + Math.pow(segmentLength, 1.0 / 2);
        height = NODE_HEIGHT;

        this.setSize(width, height);
        this.setDrawDimensionsUpToDate();
    }

    @Override
    public DrawableNode getParentSegment() {
        return this; // Don't ask!
    }

    @Override
    public DrawableNode getChildSegment() {
        return this; // Don't ask!
    }

    private double getGenomeFraction() {
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
        this.setStrokeWidth(DRAWABLE_SEGMENT_STROKE_WIDTH);

        this.setColors(fillColor, strokeColor);
    }
}
