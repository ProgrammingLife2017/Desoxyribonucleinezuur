package programminglife.model.drawing;

import javafx.scene.shape.Line;
import programminglife.model.Edge;
import programminglife.model.Genome;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A {@link programminglife.model.Edge} that also Implements {@link Drawable}.
 */
public class DrawableEdge extends Line {
   // private Edge link; // TODO: change to Edge?
    private DrawableNode parent;
    private DrawableNode child;

    /**
     * Create a Drawable edge.
     * @param link The {@link Edge} to draw
     * @param parent The {@link DrawableNode} parent of this edge
     * @param child The {@link DrawableNode} child of this edge
     */
    public DrawableEdge(DrawableNode parent, DrawableNode child) {
        //this.link = link;
        this.parent = parent;
        this.child = child;
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

    public void setStartLocation(XYCoordinate startLocation){
        this.setStartX(startLocation.getX());
        this.setStartY(startLocation.getY());
    }

    public void setEndLocation(XYCoordinate endLocation){
        this.setEndX(endLocation.getX());
        this.setEndY(endLocation.getY());
    }

}
