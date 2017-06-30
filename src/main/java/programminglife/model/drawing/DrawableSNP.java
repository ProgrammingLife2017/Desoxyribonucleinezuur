package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * A subclass of {@link DrawableNode} representing Singular Nucleotide Polymorphism.
 * Class representing a SNP.
 * Contains from 2 to 4 nodes.
 */
public class DrawableSNP extends DrawableNode {

    private DrawableNode parent;
    private DrawableNode child;
    private final Collection<DrawableSegment> mutations;

    /**
     * Construct a {@link DrawableNode}.
     *
     * @param parent    the parent Segment of the mutations/SNP
     * @param child     the child Segment of the mutations/SNP
     * @param mutations the Segments in this bubble
     * @param zoomLevel double of the zoomLevel.
     */
    DrawableSNP(DrawableNode parent, DrawableNode child, Collection<DrawableSegment> mutations, double zoomLevel) {
        super(mutations.iterator().next().getGraph(), DrawableNode.getUniqueId());

        this.parent = parent;
        this.child = child;
        this.mutations = mutations;

        this.parent.getChildren().add(this.getIdentifier());
        this.child.getParents().add(this.getIdentifier());

        this.setDrawDimensions(zoomLevel);
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
    public DrawableSNP createSNPIfPossible(SubGraph subGraph) {
        return null;
    }

    /**
     * Color this according to contents.
     *
     * @param sg the {@link SubGraph} this {@link DrawableNode} is in
     */
    @Override
    public void colorize(SubGraph sg) {
        this.setColors(Color.BLANCHEDALMOND, Color.BLACK);
        this.setStrokeWidth(3.5 * sg.getZoomLevel());
    }

    @Override
    public Collection<Integer> getGenomes() {
        return mutations.stream()
                .map(DrawableNode::getGenomes)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Set the size of this drawing.
     */
    @Override
    protected void setDrawDimensions(double zoomLevel) {
        this.setSize(new XYCoordinate(NODE_HEIGHT * zoomLevel, NODE_HEIGHT * zoomLevel));
    }

    /**
     * Set the size of this {@link Drawable}.
     *
     * @param size the preferred size
     */
    private void setSize(XYCoordinate size) {
        this.setWidth(size.getX());
        this.setHeight(size.getY());
    }

    @Override
    public DrawableNode getParentSegment() {
        return this;
    }

    @Override
    public DrawableNode getChildSegment() {
        return this;
    }

    @Override
    public Collection<Integer> getParentGenomes() {
        return this.getGenomes();
    }

    @Override
    public Collection<Integer> getChildGenomes() {
        return this.getGenomes();
    }

    public Collection<DrawableSegment> getMutations() {
        return this.mutations;
    }

    public DrawableNode getChild() {
        return child;
    }

    public DrawableNode getParent() {
        return parent;
    }
}
