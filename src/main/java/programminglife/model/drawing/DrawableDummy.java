package programminglife.model.drawing;

import programminglife.model.GenomeGraph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

/**
 * A class that handles the creation and usage of dummy nodes.
 */
public class DrawableDummy extends DrawableNode {
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
    void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parent.getIdentifier() == oldParent.getIdentifier()) {
            this.parent = newParent;
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                    oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
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

    /**
     * Get the IDs of genomes through this.
     *
     * @return the IDs of genomes
     */
    @Override
    public Collection<Integer> getGenomes() {
        return this.genomes;
    }

    @Override
    public void colorize() {
        DrawableEdge.colorize(this, this.getGraph());
    }

    @Override
    public void setLocation(int x, int y) {
        this.setX(x);
        this.setY(y + NODE_HEIGHT / 2);
    }

    @Override
    protected void setDrawDimensions() {

    }

    @Override
    public String toString() {
        return String.format("Link from %s to %s", this.parent, this.child);
    }
}
