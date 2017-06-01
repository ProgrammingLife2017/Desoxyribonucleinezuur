package programminglife.model.drawing;

import javafx.scene.shape.Line;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

/**
 * A {@link programminglife.model.Edge} that also Implements {@link Drawable}.
 */
public class DrawableEdge extends Line {
    private Link link; // TODO: change to Edge?
    private DrawableNode parent;
    private DrawableNode child;

    /**
     * Create a Drawable edge.
     * @param parent The {@link DrawableNode} parent of this edge
     * @param child The {@link DrawableNode} child of this edge
     */
    public DrawableEdge(DrawableNode parent, DrawableNode child) {
        this.parent = parent;
        this.child = child;
        this.link = parent.getLink(child);
    }

//    public Collection<Genome> getGenomes() {
//        return this.link.getGenomes();
//    }

    public DrawableNode getStart() {
        return parent;
    }

    public DrawableNode getEnd() {
        return child;
    }

    /**
     * Set the starting location of this edge.
     * @param startLocation The {@link XYCoordinate} to start drawing from.
     */
    public void setStartLocation(XYCoordinate startLocation) {
        this.setStartX(startLocation.getX());
        this.setStartY(startLocation.getY());
    }

    /**
     * Set the end location of this edge.
     * @param endLocation The {@link XYCoordinate} to end the drawing at.
     */
    public void setEndLocation(XYCoordinate endLocation) {
        this.setEndX(endLocation.getX());
        this.setEndY(endLocation.getY());
    }

    public Link getLink() {
        return link;
    }
}
