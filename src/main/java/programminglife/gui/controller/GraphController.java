package programminglife.gui.controller;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import programminglife.model.Dummy;
import programminglife.model.GenomeGraph;
import programminglife.model.Segment;
import programminglife.model.XYCoordinate;
import programminglife.model.drawing.DrawableEdge;
import programminglife.model.drawing.DrawableNode;
import programminglife.model.drawing.SubGraph;
import programminglife.utility.Console;

/**
 * Created by Martijn van Meerten on 8-5-2017.
 * Controller for drawing the graph.
 */
public class GraphController {
    private static final XYCoordinate INITIAL_OFFSET = new XYCoordinate(50, 50);
    private static final XYCoordinate HORIZONTAL_OFFSET = new XYCoordinate(50, 0);
    private static final double CHILD_OFFSET = 1.7;

    private GenomeGraph graph;
    private Group grpDrawArea;
    private int centerNodeID;

    /**
     * Initialize controller object.
     * @param graph The genome graph to control
     * @param grpDrawArea The {@link Group} to draw in
     */
    public GraphController(GenomeGraph graph, Group grpDrawArea) {
        this.graph = graph;
        this.grpDrawArea = grpDrawArea;
    }

    /**
     * Method to draw the subgraph decided by a center node and radius.
     * @param centerNode the node of which the radius starts.
     * @param radius the amount of layers to be drawn.
     */
    public void draw(int centerNode, int radius) {
        long startTimeProgram = System.nanoTime();
        centerNodeID = centerNode;
        Segment centerSegment = new Segment(graph, centerNode);
        DrawableNode center = new DrawableNode(centerSegment);
        SubGraph subGraph = new SubGraph(center, radius);

        long startLayoutTime = System.nanoTime();

        subGraph.layout();

        long startTimeDrawing = System.nanoTime();

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            drawNode(drawableNode);
            for (DrawableNode child : subGraph.getChildren(drawableNode)) {
                drawEdge(drawableNode, child);
            }
        }

        DrawableNode drawableCenterNode = subGraph.getNodes().get(centerSegment);
        drawableCenterNode.setFill(Color.ORANGE);
        double xCoord = drawableCenterNode.getX();
        double yCoord = drawableCenterNode.getY();
        grpDrawArea.setTranslateX(650 - xCoord);
        grpDrawArea.setTranslateY(350);


        long finishTime = System.nanoTime();
        long differenceTimeProgram = finishTime - startTimeProgram;
        long differenceTimeDrawing = finishTime - startTimeDrawing;
        long differenceTimeLayout = finishTime - startLayoutTime;
        long msdifferenceTimeProgram = differenceTimeProgram / 1000000;
        long milisecondTimeDrawing = differenceTimeDrawing   / 1000000;
        long msdifferenceTimeLayout = differenceTimeLayout   / 1000000;
        Console.println("Time of Drawing:  " + milisecondTimeDrawing);
        Console.println("Time of layout:  " + msdifferenceTimeLayout);
        Console.println("Time of Total Program:  " + msdifferenceTimeProgram);

    }

    /**
     * Draws a edge on the location it has.
     * @param parent {@link DrawableNode} is the node to be draw from.
     * @param child {@link DrawableNode} is the node to draw to.
     */
    private void drawEdge(DrawableNode parent, DrawableNode child) {
        DrawableEdge edge = new DrawableEdge(parent, child);
        // If either parent or child are dummy nodes make on click use the link in that dummy.
        if (parent.getNode() instanceof Dummy) {
            edge.setOnMouseClicked(event -> {
                Console.println(parent.getNode().getLink(null).toString());
            });
        } else if (child.getNode() instanceof  Dummy) {
            edge.setOnMouseClicked(event -> {
                Console.println(child.getNode().getLink(null).toString());
            });
        } else {
            edge.setOnMouseClicked(event -> Console.println(edge.toString()));
        }

        edge.setStroke(Color.DARKGRAY);
        edge.setStrokeWidth(3);
        edge.setStartLocation(edge.getStart().getRightBorderCenter());
        edge.setEndLocation(edge.getEnd().getLeftBorderCenter());
        this.grpDrawArea.getChildren().add(edge);
    }

    /**
     * Draws a node on the location it has.
     * @param drawableNode {@link DrawableNode} is the node to be drawn.
     */
    public void drawNode(DrawableNode drawableNode) {
        if (!(drawableNode.getNode() instanceof Dummy)) {
            drawableNode.setOnMouseClicked(event -> {
                Console.println(drawableNode.getSequence());
                Console.println(drawableNode.toString());
            });
        } else {
            Dummy node = (Dummy) drawableNode.getNode();
            drawableNode.setOnMouseClicked(event -> {
                Console.println(node.getLink(null).toString());
            });
        }

        drawableNode.setFill(Color.TRANSPARENT);
        drawableNode.setStroke(Color.BLUE);
        if (drawableNode.getNode() instanceof Dummy) {
            drawableNode.setStroke(Color.DARKGRAY);
            drawableNode.setStrokeWidth(3);
        }
        this.grpDrawArea.getChildren().add(drawableNode);
    }

    /**
     * Getter for the graph.
     * @return - The graph
     */
    public GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Setter for the graph.
     * @param graph The graph
     */
    void setGraph(GenomeGraph graph) {
        this.graph = graph;
    }

    /**
     * Clear the draw area.
     */
    public void clear() {
        this.grpDrawArea.getChildren().clear();
    }
}
