package programminglife.model.drawing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import programminglife.model.Dummy;
import programminglife.model.GenomeGraph;
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
        if (parent.getNode() instanceof Dummy) {
            this.link = parent.getNode().getLink(null);
        } else if (child.getNode() instanceof Dummy) {
            this.link = child.getNode().getLink(null);
        } else {
            this.link = parent.getLink(child);
        }
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

    @Override
    public String toString() {
        return this.link.toString();
    }

    /**
     * Color a {@link Shape} depending on its properties.
     * @param shape the {@link Shape} to color
     * @param link the {@link Link} belonging to the {@link DrawableNode} or {@link DrawableEdge}
     * @param graph the {@link GenomeGraph} belonging to the {@link DrawableNode} or {@link DrawableEdge}
     */
    public static void colorize(Shape shape, Link link, GenomeGraph graph) {
        double genomeFraction = graph.getGenomeFraction(link);

        double minStrokeWidth = 1.d, maxStrokeWidth = 6.5;
        double strokeWidth = minStrokeWidth + genomeFraction * (maxStrokeWidth - minStrokeWidth);

        double minBrightness = 0.6, maxBrightness = 0.25;
        double brightness = minBrightness + genomeFraction * (maxBrightness - minBrightness);

        Color strokeColor = Color.hsb(0.d, 0.d, brightness);

        shape.setStrokeWidth(strokeWidth);
        shape.setStroke(strokeColor);
    }

    /**
     * Color a {@link DrawableEdge} depending on its properties.
     * @param graph the {@link GenomeGraph} belonging to the {@link DrawableEdge}
     */
    public void colorize(GenomeGraph graph) {
        colorize(this, link, graph);
    }

    public int[] getGenomes() {
        return this.getLink().getGenomes();
    }
}
