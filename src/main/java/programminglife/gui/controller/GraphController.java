package programminglife.gui.controller;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import programminglife.gui.ResizableCanvas;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import programminglife.model.drawing.*;
import programminglife.utility.Console;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Martijn van Meerten on 8-5-2017.
 * Controller for drawing the graph.
 */
public class GraphController {

    private GenomeGraph graph;
    private double locationCenterY;
    private double locationCenterX;
    private LinkedList<DrawableNode> oldMinMaxList = new LinkedList<>();
    private SubGraph subGraph;
    private AnchorPane anchorGraphInfo;
    private LinkedList<DrawableNode> oldGenomeList = new LinkedList<>();
    private ResizableCanvas canvas;

    private double zoomLevel = 1;

    private int centerNodeInt;

    /**
     * Initialize controller object.
     * @param graph The genome graph to control
     * @param canvas the {@link Canvas} to draw in
     * @param anchorGraphInfo the {@link AnchorPane} were to show the info of a node or edge.
     */
    public GraphController(GenomeGraph graph, ResizableCanvas canvas, AnchorPane anchorGraphInfo) {
        this.graph = graph;
        this.canvas = canvas;
        this.anchorGraphInfo = anchorGraphInfo;
    }

    public int getCenterNodeInt() {
        return this.centerNodeInt;
    }

    /**
     * Method to draw the subGraph decided by a center node and radius.
     * @param center the node of which the radius starts.
     * @param radius the amount of layers to be drawn.
     */
    public void draw(int center, int radius) {
        long startTimeProgram = System.nanoTime();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        long startTimeSubGraph = System.nanoTime();

        DrawableSegment centerNode = new DrawableSegment(graph, center);
        centerNodeInt = centerNode.getIdentifier();
        subGraph = new SubGraph(centerNode, radius);

        long finishTimeSubGraph = System.nanoTime();

        long startLayoutTime = System.nanoTime();
        subGraph.layout();
        long finishTimeLayout = System.nanoTime();

        long startTimeColorize = System.nanoTime();
        colorize();
        long finishTimeColorize = System.nanoTime();


        long startTimeDrawing = System.nanoTime();
        draw(gc);
        long finishTimeDrawing = System.nanoTime();

//        long startHighlight = System.nanoTime();
        highlightNode(center, Color.DARKORANGE);
        centerOnNodeId(center);
//        Console.println("Time to highlight: " + (System.nanoTime() - startHighlight) / 1000000 + " ms");

        long finishTime = System.nanoTime();
        long differenceTimeProgram = finishTime - startTimeProgram;
        long differenceTimeDrawing = finishTimeDrawing - startTimeDrawing;
        long differenceTimeLayout = finishTimeLayout - startLayoutTime;
        long differenceTimeSubGraph = finishTimeSubGraph - startTimeSubGraph;
        long differenceTimeColorize = finishTimeColorize - startTimeColorize;
        long msDifferenceTimeProgram = differenceTimeProgram / 1000000;
        long millisecondTimeDrawing = differenceTimeDrawing / 1000000;
        long msDifferenceTimeLayout = differenceTimeLayout / 1000000;
        long msDifferenceTimeSubGraph = differenceTimeSubGraph / 1000000;
        long msDifferenceTimeColorize = differenceTimeColorize / 1000000;
        Console.println("time of SubGraph: " + msDifferenceTimeSubGraph);
        Console.println("Time of layout:  " + msDifferenceTimeLayout);
        Console.println("Time of Colorize:  " + msDifferenceTimeColorize);
        Console.println("Time of Drawing:  " + millisecondTimeDrawing);
        Console.println("Time of Total Program:  " + msDifferenceTimeProgram);
    }

    /**
     * Method to do the coloring of the to be drawn graph.
     */
    private void colorize() {
        canvas.getGraphicsContext2D().setLineWidth(3.0);
        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            drawableNode.colorize(subGraph);
        }
    }

    /**
     * Fill the rectangles with the color.
     * @param nodes the Collection of {@link Integer Integers} to highlight.
     * @param color the {@link Color} to highlight with.
     */
    private void highlightNodesByID(Collection<Integer> nodes, Color color) {
        for (int i : nodes) {
            highlightNode(i, color);
        }
    }

    /**
     * Method to highlight a collection of nodes.
     * @param nodes The nodes to highlight.
     * @param color The color to highlight with.
     */
    private void highlightNodes(Collection<DrawableNode> nodes, Color color) {
        for (DrawableNode drawNode: nodes) {
            highlightNode(drawNode, color);
        }
    }

    /**
     * Fill the rectangle with the color.
     * @param nodeID the nodeID of the node to highlight.
     * @param color the {@link Color} to highlight with.
     */
    public void highlightNode(int nodeID, Color color) {
        DrawableNode node = subGraph.getNodes().get(nodeID);
        highlightNode(node, color);
    }

    /**
     * Highlights a single node.
     * @param node {@link DrawableNode} to highlight.
     * @param color {@link Color} to color with.
     */
    public void highlightNode(DrawableNode node, Color color) {
        node.setStrokeColor(color);
        node.setStrokeWidth(5.0);
        drawNode(canvas.getGraphicsContext2D(), node);
    }

    /**
     * Method to highlight a Link. Changes the stroke color of the Link.
     * @param edge {@link DrawableEdge} is the edge to highlight.
     * @param color {@link Color} is the color in which the Link node needs to highlight.
     */
    private void highlightEdge(DrawableEdge edge, Color color) {
        edge.setStrokeColor(color);
    }

    /**
     * Method to highlight a dummy node. Changes the stroke color of the node.
     * @param node {@link DrawableDummy} is the dummy node that needs highlighting.
     * @param color {@link Color} is the color in which the dummy node needs a highlight.
     */
    private void highlightDummyNode(DrawableDummy node, Color color) {
        node.setStrokeColor(color);
    }

    /**
     * Draws a edge on the location it has.
     * @param gc {@link GraphicsContext} is the GraphicsContext required to draw.
     * @param parent {@link DrawableNode} is the node to be draw from.
     * @param child {@link DrawableNode} is the node to draw to.
     */
    private void drawEdge(GraphicsContext gc, DrawableNode parent, DrawableNode child) {
        DrawableEdge edge = new DrawableEdge(parent, child);

        edge.colorize(subGraph);

        gc.setLineWidth(edge.getStrokeWidth() * zoomLevel);
        gc.setStroke(edge.getStrokeColor());

        XYCoordinate startLocation = edge.getStartLocation();
        XYCoordinate endLocation = edge.getEndLocation();

        gc.strokeLine(startLocation.getX(), startLocation.getY(), endLocation.getX(), endLocation.getY());
    }

    /**
     * Draws a node on the location it has.
     * @param gc {@link GraphicsContext} is the GraphicsContext required to draw.
     * @param drawableNode {@link DrawableNode} is the node to be drawn.
     */
    public void drawNode(GraphicsContext gc, DrawableNode drawableNode) {
        gc.setStroke(drawableNode.getStrokeColor());
        gc.setFill(drawableNode.getFillColor());
        gc.setLineWidth(drawableNode.getStrokeWidth());

        gc.strokeRect(drawableNode.getLeftBorderCenter().getX(), drawableNode.getLocation().getY(),
                drawableNode.getWidth(), drawableNode.getHeight());
        gc.fillRect(drawableNode.getLeftBorderCenter().getX(), drawableNode.getLocation().getY(),
                drawableNode.getWidth(), drawableNode.getHeight());


//        if (!(drawableNode instanceof DrawableDummy)) {
//            drawableNode.setOnMouseClicked(event -> Console.println(drawableNode.details()));
//
//            drawableNode.setOnMouseClicked(event -> {
//                if (event.isShiftDown()) {
//                    showInfoNode((DrawableSegment) drawableNode, 250);
//                } else {
//                    showInfoNode((DrawableSegment) drawableNode, 10);
//                }
//            });
//        }


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
        this.canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
        DrawableNode drawableCenterNode = subGraph.getNodes().get(nodeId);
        double xCoordinate = drawableCenterNode.getCenter().getX();

        Bounds bounds = canvas.getParent().getLayoutBounds();
        double boundsHeight = bounds.getHeight();
        double boundsWidth = bounds.getWidth();

        locationCenterY = boundsHeight / 4;
        locationCenterX = boundsWidth / 2 - xCoordinate;

        translate(locationCenterX, locationCenterY);
    }

    /**
     * Translate function for the nodes. Used to change the location of nodes instead of mocing the canvas.
     * @param xDifference double with the value of the change in the X (horizontal) direction.
     * @param yDifference double with the value of the change in the Y (vertical) direction.
     */
    public void translate(double xDifference, double yDifference) {
        for (DrawableNode node : subGraph.getNodes().values()) {
            double oldXLocation = node.getLocation().getX();
            double oldYLocation = node.getLocation().getY();
            node.setLocation(oldXLocation + xDifference, oldYLocation + yDifference);
        }
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Zoom function for the nodes. Used to increase the size of the nodes instead of zooming in on the canvas itself.
     * @param scale double with the value of the increase of the nodes. Value higher than 1 means that the node size
     * decreases, value below 1 means that the node size increases.
     */
    public void zoom(double scale) {
        for (DrawableNode node : subGraph.getNodes().values()) {
            double oldXLocation = node.getLocation().getX();
            double oldYLocation = node.getLocation().getY();
            node.setHeight(node.getHeight() / scale);
            node.setWidth(node.getWidth() / scale);
            node.setStrokeWidth(node.getStrokeWidth() / scale);
            node.setLocation(oldXLocation / scale, oldYLocation / scale);
        }
        zoomLevel /= scale;
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Draw method for the whole subgraph. Calls draw node and draw Edge for every node and edge.
     * @param gc is the {@link GraphicsContext} required to draw.
     */
    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            drawNode(gc, drawableNode);
            for (DrawableNode child : subGraph.getChildren(drawableNode)) {
                drawEdge(gc, drawableNode, child);
            }
        }
    }

    /**
     * Method to show the information of an edge.
     * @param edge DrawableEdge the edge which has been clicked on.
     * @param x int the x location of the TextField.
     */
    private void showInfoEdge(DrawableEdge edge, int x) {
        anchorGraphInfo.getChildren().removeIf(node1 -> node1.getLayoutX() == x);

        Text idText = new Text("Genomes: "); idText.setLayoutX(x); idText.setLayoutY(65);
        Text parentsText = new Text("Parent: "); parentsText.setLayoutX(x); parentsText.setLayoutY(115);
        Text childrenText = new Text("Child: "); childrenText.setLayoutX(x); childrenText.setLayoutY(165);

        TextField id = getTextField("Genomes: ", x, 70, graph.getGenomeNames(edge.getGenomes()).toString());
        TextField parent = getTextField("Parent Node: ", x, 120, Integer.toString(edge.getStart().getIdentifier()));
        TextField child = getTextField("Child Node: ", x, 170, Integer.toString(edge.getEnd().getIdentifier()));

        anchorGraphInfo.getChildren().addAll(idText, parentsText, childrenText, id, parent, child);
    }

    /**
     * Method to show the information of a node.
     * @param node DrawableSegment the node which has been clicked on.
     * @param x int the x location of the TextField.
     */
    private void showInfoNode(DrawableSegment node, int x) {
        Text idText = new Text("ID: "); idText.setLayoutX(x); idText.setLayoutY(65);
        Text parentText = new Text("Parents: "); parentText.setLayoutX(x); parentText.setLayoutY(105);
        Text childText = new Text("Children: "); childText.setLayoutX(x); childText.setLayoutY(145);
        Text inEdgeText = new Text("Incoming Edges: "); inEdgeText.setLayoutX(x); inEdgeText.setLayoutY(185);
        Text outEdgeText = new Text("Outgoing Edges: "); outEdgeText.setLayoutX(x); outEdgeText.setLayoutY(225);
        Text seqLengthText = new Text("Sequence Length: "); seqLengthText.setLayoutX(x); seqLengthText.setLayoutY(265);
        Text genomeText = new Text("Genomes: "); genomeText.setLayoutX(x); genomeText.setLayoutY(305);
        Text seqText = new Text("Sequence: "); seqText.setLayoutX(x); seqText.setLayoutY(370);

        anchorGraphInfo.getChildren().removeIf(node1 -> node1.getLayoutX() == x);

        TextField idTextField = getTextField("ID: ", x, 70, Integer.toString(node.getIdentifier()));

        StringBuilder parentSB = new StringBuilder();
        node.getParents().forEach(id -> parentSB.append(id).append(", "));
        TextField parents;
        if (parentSB.length() > 2) {
            parentSB.setLength(parentSB.length() - 2);
            parents = getTextField("Parents: ", x, 110, parentSB.toString());
        } else {
            parentSB.replace(0, parentSB.length(), "This node has no parent(s)");
            parents = getTextField("Parents: ", x, 110, parentSB.toString());
        }

        StringBuilder childSB = new StringBuilder();
        node.getChildren().forEach(id -> childSB.append(id).append(", "));
        TextField children;
        if (childSB.length() > 2) {
            childSB.setLength(childSB.length() - 2);
            children = getTextField("Children: ", x, 150, childSB.toString());
        } else {
            childSB.replace(0, childSB.length(), "This node has no child(ren)");
            children = getTextField("Children: ", x, 150, childSB.toString());
        }

        String genomesString = graph.getGenomeNames(node.getGenomes()).toString();
        String sequenceString = node.getSequence().replaceAll("(.{24})", "$1" + System.getProperty("line.separator"));
        TextField inEdges = getTextField("Incoming Edges: ", x, 190, Integer.toString(node.getParents().size()));
        TextField outEdges = getTextField("Outgoing Edges: ", x, 230, Integer.toString(node.getChildren().size()));
        TextField seqLength = getTextField("Sequence Length: ", x, 270, Integer.toString(node.getSequence().length()));
        TextArea genome = getTextArea("Genome: ", x, 310, genomesString.substring(1, genomesString.length() - 1), 40);
        genome.setWrapText(true);
        TextArea seq = getTextArea("Sequence: ", x, 375, sequenceString, 250);
        anchorGraphInfo.getChildren().addAll(idText, parentText, childText, inEdgeText,
                outEdgeText, genomeText, seqLengthText, seqText);
        anchorGraphInfo.getChildren().addAll(idTextField, parents, children, inEdges, outEdges, genome, seqLength, seq);
    }

    /**
     * Returns a textField to be used by the edge and node information show panel.
     * @param id String the id of the textField.
     * @param x int the x coordinate of the textField inside the anchorPane.
     * @param y int the y coordinate of the textField inside the anchorPane.
     * @param text String the text to be shown by the textField.
     * @param height int of the height of the area.
     * @return TextField the created textField.
     */
    private TextArea getTextArea(String id, int x, int y, String text, int height) {
        TextArea textArea = new TextArea();
        textArea.setId(id);
        textArea.setText(text);
        textArea.setLayoutX(x);
        textArea.setLayoutY(y);
        textArea.setEditable(false);
        textArea.setStyle("-fx-text-box-border: transparent;-fx-background-color: none; -fx-background-insets: 0;"
                + " -fx-padding: 1 3 1 3; -fx-focus-color: transparent; "
                + "-fx-faint-focus-color: transparent; -fx-font-family: monospace;");
        textArea.setPrefSize(225, height);

        return textArea;
    }

    /**
     * Returns a textField to be used by the edge and node information show panel.
     * @param id String the id of the textField.
     * @param x int the x coordinate of the textField inside the anchorPane.
     * @param y int the y coordinate of the textField inside the anchorPane.
     * @param text String the text to be shown by the textField.
     * @return TextField the created textField.
     */
    private TextField getTextField(String id, int x, int y, String text) {
        TextField textField = new TextField();
        textField.setId(id);
        textField.setText(text);
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setEditable(false);
        textField.setStyle("-fx-text-box-border: transparent;-fx-background-color: none; -fx-background-insets: 0;"
                + " -fx-padding: 1 3 1 3; -fx-focus-color: transparent; "
                + "-fx-faint-focus-color: transparent; -fx-font-family: monospace;");
        textField.setPrefSize(220, 20);

        return textField;
    }

    /**
     * Method to do highlighting based on a min and max amount of genomes in a node.
     * @param min The minimal amount of genomes
     * @param max The maximal amount of genomes
     * @param color the {@link Color} in which the highlight has to be done.
     */
    public void highlightMinMax(int min, int max, Color color) {
        LinkedList<DrawableNode> drawNodeList = new LinkedList<>();

        removeHighlight(oldMinMaxList);
        removeHighlight(oldGenomeList);
        for (DrawableNode drawableNode: subGraph.getNodes().values()) {
            if (drawableNode != null && !(drawableNode instanceof DrawableDummy)) {
                int genomeCount = drawableNode.getGenomes().size();
                if (genomeCount >= min && genomeCount <= max) {
                    drawNodeList.add(drawableNode);
                }
            }
        }
        oldMinMaxList = drawNodeList;
        highlightNodes(drawNodeList, color);
    }

    /**
     * Resets the node highlighting to remove highlights.
     * @param nodes are the nodes to remove the highlight from.
     */
    private void removeHighlight(Collection<DrawableNode> nodes) {
        for (DrawableNode node: nodes) {
            node.colorize(subGraph);
        }
        this.draw(canvas.getGraphicsContext2D());
    }


    /**
     * Highlights based on genomeID.
     * @param genomeID the GenomeID to highlight on.
     */
    public void highlightByGenome(int genomeID) {
        LinkedList<DrawableNode> drawNodeList = new LinkedList<>();
        removeHighlight(oldGenomeList);
        removeHighlight(oldMinMaxList);
        for (DrawableNode drawableNode: subGraph.getNodes().values()) {
            Collection<Integer> genomes = drawableNode.getGenomes();
            for (int genome : genomes) {
                if (genome == genomeID && !(drawableNode instanceof DrawableDummy)) {
                    drawNodeList.add(drawableNode);
                }
            }
        }
        oldGenomeList = drawNodeList;
        highlightNodes(drawNodeList, Color.YELLOW);
    }
}
