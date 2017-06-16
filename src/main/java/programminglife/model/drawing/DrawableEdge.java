package programminglife.model.drawing;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * A link that also Implements {@link Drawable}.
 */
public class DrawableEdge extends Line implements Drawable {
    private DrawableNode parent;
    private DrawableNode child;
    private Collection<Integer> genomes;

    /**
     * Create a Drawable edge.
     * @param parent The {@link DrawableSegment} parent of this edge
     * @param child The {@link DrawableSegment} child of this edge
     */
    public DrawableEdge(DrawableNode parent, DrawableNode child) {
        this.parent = parent;
        this.child = child;
        this.genomes = new LinkedHashSet<>();
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

    @Override
    public String toString() {
        return String.format("Link from %s to %s", this.parent, this.child);
    }

    /**
     * Color a {@link Shape} depending on its properties.
     * @param shape the {@link Shape} to color
     * @param graph the {@link GenomeGraph} belonging to the {@link DrawableSegment} or {@link DrawableEdge}
     */
    public static void colorize(Shape shape, GenomeGraph graph) {
        double genomeFraction = 1.d;

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
        colorize(this, graph);
    }

    @Override
    public Collection<Integer> getGenomes() {
        return this.genomes;
    }

    /**
     * Add genomes in/through this {@link Drawable}.
     *
     * @param genomes a {@link Collection} of genome IDs
     */
    @Override
    public void addGenomes(Collection<Integer> genomes) {
        this.genomes.addAll(genomes);
    }
}
