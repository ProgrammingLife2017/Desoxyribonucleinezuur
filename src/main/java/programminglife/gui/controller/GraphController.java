package programminglife.gui.controller;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
    private Group rectangleGroup;
    private static final XYCoordinate DEFAULT_OFFSET = new XYCoordinate(5, 0);

    /**
     * Initialize controller object.
     * @param graph The genome graph to control
     * @param canvas The canvas on which to draw
     */
    public GraphController(Graph graph, Canvas canvas, Group rectangleGroup) {
        this.graph = graph;
        this.canvas = canvas;
        this.rectangleGroup = rectangleGroup;

        this.canvas.setVisible(true);
        this.graphicsContext = this.canvas.getGraphicsContext2D();
    }

    /**
     * Draw the {@link Graph} with DFS from {@link Node} 1.
     * @param maxDepth The max depth of child {@link Node}s to draw
     */
    public void draw(int maxDepth) {
        this.drawDFS(null, this.getGraph().getNode(1), new XYCoordinate(10, 10), maxDepth);
    }

    /**
     * Draw all nodes recursively on the screen.
     *
     * @param origin
     * @param node Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @return a {@link Set} of all drawn {@link Node}s
     */
    private Set<Node> drawDFS(Node origin, Node node, XYCoordinate offset) {
        return this.drawDFS(origin, node, offset, -1, new HashSet<>());
    }

    /**
     * Draw all nodes recursively on the screen.
     *
     * @param origin
     * @param node Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @param maxDepth The max depth from root to draw nodes
     * @return a {@link Set} of all drawn {@link Node}s
     */
    public Set<Node> drawDFS(Node origin, Node node, XYCoordinate offset, int maxDepth) {
        return this.drawDFS(origin, node, offset, maxDepth, new HashSet<>());
    }

    /**
     * Draw all nodes recursively on the screen.
     * @param origin
     * @param node Draw this node and all its children recursively
     * @param offset Draws nodes at this offset from the top-left of the screen
     * @param drawnNodes A set containing all drawn nodes
     */
    private Set<Node> drawDFS(Node origin, Node node, XYCoordinate offset, int maxDepth, Set<Node> drawnNodes) {
        XYCoordinate dimensions = this.drawNode(origin, node, offset);

        if (null != origin) {
            XYCoordinate targetLeft = node.getLeftBorderCenter();
            XYCoordinate originRight = origin.getRightBorderCenter();

            Line link = new Line(targetLeft.getX(), targetLeft.getY(), originRight.getX(), originRight.getY());
            link.setFill(Color.BLACK);
            this.rectangleGroup.getChildren().add(link);
        }

        int childCount = 0;
        for (Node child : node.getChildren()) {
            if (maxDepth == 0 || drawnNodes.contains(child)) {
                continue;
            }

            drawnNodes.add(child);

            XYCoordinate newOffset = offset.setY(childCount * 15).add(dimensions).add(DEFAULT_OFFSET);
            this.drawDFS(node, child, newOffset, maxDepth - 1, drawnNodes);
            childCount++;
        }

        return drawnNodes;
    }

    /**
     * Draws the node on the canvas.
     *
     * @param origin
     * @param nodeID The ID of the node to draw
     * @param offset Draw the node at this offset
     * @return The size of the node
     */
    private XYCoordinate drawNode(Node origin, int nodeID, XYCoordinate offset) {
        return this.drawNode(origin, this.graph.getNode(nodeID), offset);
    }

    /**
     *
     *
     * @param origin
     * @param node
     * @param offset
     * @return
     */
    private XYCoordinate drawNode(Node origin, Node node, XYCoordinate offset) {
        int segmentLength = node.getSequence().length();
        int width, height;

        width = (int) Math.pow(segmentLength, 1.0 / 2);
        height = 10;

        width = Math.max(width, 10);
        height = Math.max(height, 10);

        node.setLocation(offset);
        node.setSize(new XYCoordinate(width, height));

        node.setOnMouseClicked(event -> System.out.printf("%s was clicked!\n", node.toString()));
        node.setFill(Color.BLACK);

        this.rectangleGroup.getChildren().add(node);

//        this.graphicsContext.setStroke(Color.color(Math.random(), Math.random(), Math.random()));
//        this.graphicsContext.strokeRect(offset.getX(), offset.getY(), width, height);

        return node.getSize();
    }

    /**
     * Getter for the graph.
     * @return - The graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Setter for the graph.
     * @param graph The graph
     */
    void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Clear the canvas.
     */
    public void clear() {
        this.graphicsContext.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }
}
