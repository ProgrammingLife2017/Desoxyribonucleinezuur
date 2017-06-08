package programminglife.gui.controller;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import programminglife.model.Dummy;
import programminglife.model.GenomeGraph;
import programminglife.model.Segment;
import programminglife.model.drawing.DrawableEdge;
import programminglife.model.drawing.DrawableNode;
import programminglife.model.drawing.SubGraph;
import programminglife.utility.Console;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Martijn van Meerten on 8-5-2017.
 * Controller for drawing the graph.
 */
public class GraphController {

    private GenomeGraph graph;
    private Group grpDrawArea;
    private double locationCenterY;
    private double locationCenterX;
    private SubGraph subGraph;
    private AnchorPane anchorGraphInfo;

    /**
     * Initialize controller object.
     * @param graph The genome graph to control
     * @param grpDrawArea The {@link Group} to draw in
     * @param anchorGraphInfo the {@link AnchorPane} were to show the info of a node or edge.
     */
    public GraphController(GenomeGraph graph, Group grpDrawArea, AnchorPane anchorGraphInfo) {
        this.graph = graph;
        this.grpDrawArea = grpDrawArea;
        this.anchorGraphInfo = anchorGraphInfo;
    }

    /**
     * Method to draw the subgraph decided by a center node and radius.
     * @param center the node of which the radius starts.
     * @param radius the amount of layers to be drawn.
     */
    public void draw(int center, int radius) {
        long startTimeProgram = System.nanoTime();
        Segment centerSegment = new Segment(graph, center);
        DrawableNode centerNode = new DrawableNode(centerSegment);
        subGraph = new SubGraph(centerNode, radius);

        long startLayoutTime = System.nanoTime();

        subGraph.layout();

        long startTimeDrawing = System.nanoTime();

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            drawNode(drawableNode);
            for (DrawableNode child : subGraph.getChildren(drawableNode)) {
                drawEdge(drawableNode, child);
            }
        }

        centerOnNodeId(center);
        highlightNode(centerNode, Color.ORANGE);


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
     * Fill the rectangles with the color.
     * @param nodes the Collection of {@link DrawableNode} to highlight.
     * @param color the {@link Color} to highlight with.
     */
    private void highlightNodes(Collection<DrawableNode> nodes, Color color) {
        for (DrawableNode dwnode : nodes) {
            highlightNode(dwnode, color);
        }
    }

    /**
     * Fill the rectangle with the color.
     * @param centerNode the {@link DrawableNode} to highlight.
     * @param color the {@link Color} to highlight with.
     */
    private void highlightNode(DrawableNode centerNode, Color color) {
        centerNode.setFill(color);
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
        edge.setOnMouseClicked(event -> {
            if (event.isShiftDown()) {
                showInfoEdge(edge, 140);
            } else {
                showInfoEdge(edge, 10);
            }
        });
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
            drawableNode.setFill(Color.TRANSPARENT);
            drawableNode.setStroke(Color.BLUE);
            drawableNode.setOnMouseClicked(event -> {
                Console.println(drawableNode.getSequence());
                Console.println(drawableNode.toString());
            });
        } else {
            drawableNode.setStroke(Color.DARKGRAY);
            drawableNode.setStrokeWidth(3);
            Dummy node = (Dummy) drawableNode.getNode();
            drawableNode.setOnMouseClicked(event -> Console.println(node.getLink(null).toString()));
        }
        drawableNode.setOnMouseClicked(event -> {
            if (event.isShiftDown()) {
                showInfoNode(drawableNode, 140);
            } else {
                showInfoNode(drawableNode, 10);
            }
        });
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

    public double getLocationCenterX() {
        return locationCenterX;
    }


    public double getLocationCenterY() {
        return locationCenterY;
    }

    /**
     * Centers on the given node.
     * @param nodeId is the node to center on.
     */
    public void centerOnNodeId(int nodeId) {
        DrawableNode drawableCenterNode = subGraph.getNodes().get(new Segment(graph, nodeId));
        double xCoord = drawableCenterNode.getX();

        Bounds bounds = grpDrawArea.getParent().getLayoutBounds();
        double boundsHeight = bounds.getHeight();
        double boundsWidth = bounds.getWidth();

        locationCenterY = boundsHeight / 4;
        locationCenterX = boundsWidth / 2 - xCoord;

        grpDrawArea.setTranslateX(locationCenterX);
        grpDrawArea.setTranslateY(locationCenterY);
    }

    private void showInfoEdge(DrawableEdge edge, int x) {
        Text idText = new Text("Genomes"); idText.setLayoutX(x); idText.setLayoutY(50);
        Text parentsText = new Text("Parent"); parentsText.setLayoutX(x); parentsText.setLayoutY(100);
        Text childrenText = new Text("Child"); childrenText.setLayoutX(x); childrenText.setLayoutY(150);

        anchorGraphInfo.getChildren().removeIf(node1 -> node1.getLayoutX() == x);

        TextField id = getTextField("Genomes: ", x, 60, Arrays.toString(edge.getLink().getGenomes()));
        TextField parent = getTextField("Parent Node: ", x, 110, edge.getStart().toString());
        TextField child = getTextField("Child Node: ", x, 160, edge.getEnd().toString());
        anchorGraphInfo.getChildren().addAll(idText, parentsText, childrenText, id, parent, child);
    }

    private void showInfoNode(DrawableNode node, int x) {
        Text idText = new Text("ID"); idText.setLayoutX(x); idText.setLayoutY(50);
        Text parentText = new Text("Parents"); parentText.setLayoutX(x); parentText.setLayoutY(100);
        Text childText = new Text("Children"); childText.setLayoutX(x); childText.setLayoutY(150);
        Text inEdgeText = new Text("Incoming Edges"); inEdgeText.setLayoutX(x); inEdgeText.setLayoutY(200);
        Text outEdgeText = new Text("Outgoing Edges"); outEdgeText.setLayoutX(x); outEdgeText.setLayoutY(250);
        Text genomeText = new Text("Genome"); genomeText.setLayoutX(x); genomeText.setLayoutY(300);
        Text seqText = new Text("Sequence"); seqText.setLayoutX(x); seqText.setLayoutY(350);

        anchorGraphInfo.getChildren().removeIf(node1 -> node1.getLayoutX() == x);


        TextField id = getTextField("ID: ", x, 60, node.getNode().getIdentifier() + "");
        TextField parents = getTextField("Parents: ", x, 110, node.getNode().getParents().toString());
        TextField children = getTextField("Children: ", x, 160, node.getNode().getChildren().toString());
        TextField inEdges = getTextField("Incoming Edges: ", x, 210, node.getNode().getParentEdges().size() + "");
        TextField outEdges = getTextField("Outgoing Edges: ", x, 260, node.getNode().getChildEdges().size() + "");
        TextField genome = getTextField("Genome: ", x, 310, Arrays.toString(node.getNode().getGenomes()));
        TextField seq = getTextField("Sequence: ", x, 360, node.getNode().getSequence());

        anchorGraphInfo.getChildren().addAll(idText, parentText, childText, inEdgeText, outEdgeText, genomeText, seqText);
        anchorGraphInfo.getChildren().addAll(id, parents, children, inEdges, outEdges, genome, seq);
    }

    private TextField getTextField(String id, int x, int y, String text) {
        TextField textField = new TextField();
        textField.setId(id);
        textField.setText(text);
        textField.setEditable(false);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefSize(120, 25);

        return textField;
    }
}
