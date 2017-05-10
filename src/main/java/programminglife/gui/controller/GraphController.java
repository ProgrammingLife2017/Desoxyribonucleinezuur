package programminglife.gui.controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import programminglife.model.Graph;
import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Martijn van Meerten on 8-5-2017.
 * Controller for drawing the graph.
 */
public class GraphController {
    private Graph graph;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private static final XYCoordinate DEFAULT_OFFSET = new XYCoordinate(5, 0);

    /**
     * Initialize controller object.
     * @param graph The genome graph to control
     * @param canvas The canvas on which to draw
     */
    public GraphController(Graph graph, Canvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
        this.canvas.setVisible(true);
        this.graphicsContext = this.canvas.getGraphicsContext2D();
    }

    /**
     * Draw all nodes recursively on the screen.
     * @param root Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @return a {@link Set} of all drawn {@link Node}s
     */
    public Set<Node> drawDFS(Node root, XYCoordinate offset) {
        return this.drawDFS(root, offset, -1, new HashSet<>());
    }

    /**
     * Draw all nodes recursively on the screen.
     * @param root Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @param maxDepth The max depth from root to draw nodes
     * @return a {@link Set} of all drawn {@link Node}s
     */
    public Set<Node> drawDFS(Node root, XYCoordinate offset, int maxDepth) {
        return this.drawDFS(root, offset, maxDepth, new HashSet<>());
    }

    /**
     * Draw all nodes recursively on the screen.
     * @param root Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @param drawnNodes A set containing all drawn nodes
     */
    private Set<Node> drawDFS(Node root, XYCoordinate offset, int maxDepth, Set<Node> drawnNodes) {
        XYCoordinate previousDimensions = this.drawNode(root, offset);

        int childCount = 0;
        for (Node child : root.getChildren()) {
            if (maxDepth == 0 || drawnNodes.contains(child)) {
                continue;
            }

            drawnNodes.add(child);

            XYCoordinate newOffset = offset.setY(childCount * 15).add(previousDimensions).add(DEFAULT_OFFSET);
            this.drawDFS(child, newOffset, maxDepth - 1, drawnNodes);
            childCount++;
        }

        return drawnNodes;
    }

    /**
     * Draws the node on the canvas.
     * @param nodeID The ID of the node to draw
     * @param offset Draw the node at this offset
     * @return The size of the node
     */
    private XYCoordinate drawNode(int nodeID, XYCoordinate offset) {
        return this.drawNode(this.graph.getNode(nodeID), offset);
    }

    /**
     *
     * @param node
     * @param offset
     * @return
     */
    private XYCoordinate drawNode(Node node, XYCoordinate offset) {
        float widthHeigthRatio = 0.2f;
        int segmentLength = node.getSequence().length() * 20;
        int width = (int) (segmentLength * (1.f - widthHeigthRatio));
        int height = (int) (segmentLength * widthHeigthRatio);

        width = 10;
        height = 10;

        this.graphicsContext.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
        this.graphicsContext.strokeRect(offset.getX(), offset.getY(), width, height);

        return XYCoordinate.coord(width, height);
    }

    /**
     * Getter for the graph.
     * @return - The graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Draws a subgraph on the canvas based on center node and radius.
     * @param graphicsContext - The graphicsContext used by the canvas
     * @param n - The node to draw
     * @return - A set of all neighbouring nodes
     */
    public Set<Node> drawSubGraph(GraphicsContext graphicsContext, Node n) {
        graphicsContext.rect(n.getSequence().length(), 20, 20, 20);
        Set<Node> nodes = n.getChildren();
        nodes.addAll(n.getParents());
        return nodes;
    }

    /**
     * Setter for the graph.
     * @param graph The graph
     */
    void setGraph(Graph graph) {
        this.graph = graph;
    }
}
