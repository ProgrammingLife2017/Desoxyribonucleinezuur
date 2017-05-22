package programminglife.model.drawing;

import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.Set;

// TODO: rename to better name
public class Drawing {
    /**
     * How much do we go further than radius to make sure we don't miss any important nodes?
     */
    private static final int OVERSHOOT = 2;

    private Set<Node> nodes;
    private Node centerNode;
    private int radius;

    public Drawing(Node centerNode, int radius) {
        // TODO
        // tactic: first go to all parents at exactly radius, then find all children of those parents
    }

    public void draw() {
        // TODO
        // use layout if not done already
    }

    public Drawable atLocation(XYCoordinate loc) {
        // TODO
        return null;
    }

    public void layout() {
        // TODO
        // note: in case of ambiguity in choosing what node to draw first, use node with lowest id
        // (to break ties and make layout deterministic)
    }

    public void setCenterNode(Node centerNode) {
        // TODO
        // drop nodes that are now outside radius
        // include nodes that have come into radius
        // drop nodes that are only connected via nodes now outside radius?
    }

    public void setRadius(int radius) {
        // TODO
        // when getting bigger: include new nodes
        // when getting smaller: drop nodes outside new radius.
    }
}
