package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Created by toinehartman on 19/06/2017.
 */
public class DrawableSNP extends DrawableNode {
    private DrawableNode parent;
    private DrawableNode child;
    final private Collection<DrawableNode> mutations;

    /**
     * Construct a {@link DrawableNode}.
     *
     * @param segments the Segments in this bubble
     */
    DrawableSNP(DrawableNode parent, DrawableNode child, Collection<DrawableNode> segments) {
        super(segments.iterator().next().getGraph(), -segments.hashCode());

        this.parent = parent;
        this.child = child;
        this.mutations = segments;
    }

    /**
     * Get the IDs of children of this.
     *
     * @return IDs of drawable children
     */
    @Override
    public Collection<Integer> getChildren() {
        return Collections.singleton(this.child.getIdentifier());
    }

    /**
     * Get the IDs of parents of this.
     *
     * @return IDs of drawable parents.
     */
    @Override
    public Collection<Integer> getParents() {
        return Collections.singleton(this.parent.getIdentifier());
    }

    /**
     * Replace a parent with another one.
     *
     * @param oldParent the parent to replace
     * @param newParent the new parent
     */
    @Override
    void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parent.getIdentifier() == oldParent.getIdentifier()) {
            this.parent = newParent;
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not the parent of this SNP (%d).",
                            oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    /**
     * Replace child with another one.
     *
     * @param oldChild the child to replace
     * @param newChild the new child
     */
    @Override
    void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (this.child.getIdentifier() == oldChild.getIdentifier()) {
            this.child = newChild;
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not the parent of this SNP (%d).",
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
        return this.mutations.toString();
    }

    @Override
    public DrawableSegment hasSNPChildren(SubGraph subGraph) {
        return null;
    }

    /**
     * Color this according to contents.
     *
     * @param sg the {@link SubGraph} this {@link DrawableNode} is in
     */
    @Override
    public void colorize(SubGraph sg) {
        this.setStroke(Color.RED);
        this.setStrokeWidth(4.d);
    }

    /**
     * Set the location to draw this.
     *
     * @param x the x location
     * @param y the y location
     */
    @Override
    void setLocation(int x, int y) {

    }

    /**
     * Set the size of this drawing.
     */
    @Override
    protected void setDrawDimensions() {
        int segmentLength = 1;
        int width, height;

        width = 10 + (int) Math.pow(segmentLength, 1.0 / 2);
        height = NODE_HEIGHT;

        this.setSize(new XYCoordinate(width, height));
        this.setDrawDimensionsUpToDate(true);
    }

    void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
        this.setDrawDimensionsUpToDate(true);
    }

    @Override
    public DrawableNode getParentSegment() {
        return parent;
    }

    @Override
    public DrawableNode getChildSegment() {
        return child;
    }
}
