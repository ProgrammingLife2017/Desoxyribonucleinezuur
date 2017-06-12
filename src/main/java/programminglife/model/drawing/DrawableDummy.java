package programminglife.model.drawing;

import programminglife.model.GenomeGraph;
import programminglife.model.Link;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

/**
 * A class that handles the creation and usage of dummy nodes.
 */
public class DrawableDummy extends DrawableNode {
    private int parentID;
    private int childID;
    private Link link;

    /**
     * Create a dummy node.
     * @param childID ID of the child node.
     * @param parentID ID of the parent node.
     */
    public DrawableDummy(int id, int childID, int parentID, GenomeGraph graph) {
        super(graph, id);

        this.parentID = parentID;
        this.childID = childID;
        this.link = graph.getLink(parentID, childID);
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
    void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parentID == oldParent.getIdentifier()) {
            this.parentID = newParent.getIdentifier();
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                    oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (this.parentID == oldChild.getIdentifier()) {
            this.parentID = newChild.getIdentifier();
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
    public void colorize(GenomeGraph graph) {
        DrawableEdge.colorize(this, this.link, this.getGraph());
    }

    @Override
    public void setLocation(int x, int y) {
        this.setX(x);
        this.setY(y + 5);
    }

    @Override
    public Link getLink(DrawableNode child) {
        return this.link;
    }

    @Override
    protected void setDrawDimensions() {

    }
}