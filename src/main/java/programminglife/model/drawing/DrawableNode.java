package programminglife.model.drawing;

import javafx.scene.shape.Rectangle;
import programminglife.model.*;

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
        HashSet<DrawableEdge> childEdges = new HashSet<DrawableEdge>();
        for (Node n : nodes) {
            for (Edge e: n.getChildEdges()){
                childEdges.add(new DrawableEdge(e, this, new DrawableNode(e.getEnd())));
            }
        }
        return childEdges;
    }

    public Collection<DrawableEdge> getParentEdges() {
        HashSet<DrawableEdge> parentEdges = new HashSet<DrawableEdge>();
        for (Node n : nodes) {
            for (Edge e : n.getParentEdges()) {
                parentEdges.add(new DrawableEdge(e, new DrawableNode(e.getStart()), this));
            }
        }
        return parentEdges;
    }

    public Collection<DrawableNode> getChildren() {
        Collection<DrawableNode> children = new HashSet<>();
        for (Node n : this.nodes) {
            for (Node child : n.getChildren()) {
                children.add(new DrawableNode(child));
            }
        }
        return children;
    }

    public Collection<DrawableNode> getParents() {
        Collection<DrawableNode> parents = new HashSet<>();
        for (Node n : this.nodes) {
            for (Node parent : n.getParents()) {
                parents.add(new DrawableNode(parent));
            }
        }
        return parents;
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
