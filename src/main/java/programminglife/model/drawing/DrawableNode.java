package programminglife.model.drawing;

import javafx.scene.shape.Rectangle;
import programminglife.model.Node;
import programminglife.model.Segment;
import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Segment} that also Implements {@link Drawable}.
 */
public class DrawableNode extends Rectangle {
    private Set<Node> nodes;

    /**
     * Create a DrawableSegment from a Segment.
     * @param node The segment to create this DrawableSegment from.
     */
    public DrawableNode(Node node) {
        this.nodes = new HashSet<>();
        nodes.add(node);
    }

    /**
     * The
     * @param nodes The set of Segments this DrawableNode
     */
    public DrawableNode(Set<Node> nodes) {
        this.nodes = nodes;
    }

    // TODO: implement methods

    public Collection<DrawableEdge> getChildEdges() {
        // TODO: implement
        throw new Error("Not implemented yet");
    }

    public Collection<DrawableEdge> getParentEdges() {
        // TODO: implement
        throw new Error("Not implemented yet");
    }

    public Collection<DrawableNode> getChildren() {
        // TODO: implement
        throw new Error("Not implemented yet");
    }

    public Collection<DrawableNode> getParents() {
        // TODO: implement
        throw new Error("Not implemented yet");
    }

    public void setLocation(XYCoordinate newLoc) {
        this.setHeight(newLoc.getY());
        this.setWidth(newLoc.getX());
    }

    public XYCoordinate getLocation() {
        return new XYCoordinate(this.getIntWidth(), this.getIntHeight());
    }

    public XYCoordinate getDimensions() {
        return new XYCoordinate(this.getIntX(), this.getIntY());
    }

    public void setDimension(XYCoordinate dimensions) {
        this.setX(dimensions.getX());
        this.setY(dimensions.getY());
    }

    public int getIntX() {
        return (int) Math.ceil(this.getX());
    }

    public int getIntY() {
        return (int) Math.ceil(this.getY());
    }

    public int getIntWidth() {
        return (int) Math.ceil(this.getWidth());
    }

    public int getIntHeight() {
        return (int) Math.ceil(this.getHeight());
    }
}
