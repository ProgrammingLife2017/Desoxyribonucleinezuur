package programminglife.model.drawing;

import programminglife.model.Genome;
import programminglife.model.Link;

import java.util.Collection;

/**
 * A {@link programminglife.model.Edge} that also Implements {@link Drawable}.
 */
public class DrawableEdge {
    private Link link; // TODO: change to Edge?
    private DrawableNode parent;
    private DrawableNode child;

    public DrawableEdge(Link link, DrawableNode parent, DrawableNode child) {
        this.link = link;
        this.parent = parent;
        this.child = child;
    }

    public Collection<Genome> getGenomes() {
        return this.link.getGenomes();
    }

    public DrawableNode getStart() {
        return parent;
    }

    public DrawableNode getEnd() {
        return child;
    }

}
