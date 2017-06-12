package programminglife.model.drawing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import programminglife.model.GenomeGraph;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

/**
 * A {@link programminglife.model.Link} that also Implements {@link Drawable}.
 */
public class DrawableEdge extends Line implements Drawable {
    private Link link;
    private DrawableNode parent;
    private DrawableNode child;

    /**
     * Create a Drawable edge.
     * @param parent The {@link DrawableSegment} parent of this edge
     * @param child The {@link DrawableSegment} child of this edge
     */
    public DrawableEdge(DrawableNode parent, DrawableNode child) {
        this.parent = parent;
        this.child = child;
        this.link = parent.getLink(child);
    }

    public DrawableNode getStart() {
        return parent;
    }

    public DrawableNode getEnd() {
        return child;
    }

    /**
     * Set the starting location of this edge.
     * @param startNode The {@link XYCoordinate} to start drawing from.
     */
    public void setStartNode(DrawableNode startNode) {
        XYCoordinate rightBorderCenter = startNode.getRightBorderCenter();
        this.setStartX(rightBorderCenter.getX());
        this.setStartY(rightBorderCenter.getY());
    }

    /**
     * Set the end location of this edge.
     * @param endNode The {@link XYCoordinate} to end the drawing at.
     */
    public void setEndNode(DrawableNode endNode) {
        XYCoordinate leftBorderCenter = endNode.getLeftBorderCenter();
        this.setEndX(leftBorderCenter.getX());
        this.setEndY(leftBorderCenter.getY());
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
     * @param link the {@link Link} belonging to the {@link DrawableSegment} or {@link DrawableEdge}
     * @param graph the {@link GenomeGraph} belonging to the {@link DrawableSegment} or {@link DrawableEdge}
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
