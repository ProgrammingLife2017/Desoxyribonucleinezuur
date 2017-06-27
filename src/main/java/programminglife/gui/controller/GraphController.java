package programminglife.gui.controller;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import programminglife.gui.ResizableCanvas;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import programminglife.model.drawing.*;
import programminglife.utility.Console;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Controller for drawing the graph.
 */
class GraphController {

    private GenomeGraph graph;
    private double locationCenterY;
    private double locationCenterX;
    private SubGraph subGraph;
    private final ResizableCanvas canvas;
    private final int archFactor = 5;

    private DrawableSegment clicked1;
    private DrawableSegment clickedShift;
    private DrawableSNP clickedSNP1;
    private DrawableSNP clickedSNPShift;

    private int centerNodeInt;
    private boolean drawSNP = false;

    private HighlightController highlightController;

    /**
     * Initialize controller object.
     *
     * @param canvas the {@link Canvas} to draw in
     */
    GraphController(ResizableCanvas canvas) {
        this.graph = null;
        this.canvas = canvas;
        this.highlightController = null;
    }

    public int getCenterNodeInt() {
        return this.centerNodeInt;
    }

    /**
     * Utility function for benchmarking purposes.
     *
     * @param description the description to print
     * @param r           the {@link Runnable} to run/benchmark
     */
    private void time(String description, Runnable r) {
        long start = System.nanoTime();
        r.run();
        Console.println(String.format("%s: %d ms", description, (System.nanoTime() - start) / 1000000));
    }

    /**
     * Method to draw the subGraph decided by a center node and radius.
     *
     * @param center the node of which the radius starts.
     * @param radius the amount of layers to be drawn.
     */
    public void draw(int center, int radius) {
        time("Total drawing", () -> {
            DrawableSegment centerNode = new DrawableSegment(graph, center, 1);
            centerNodeInt = centerNode.getIdentifier();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            time("Find subgraph", () -> subGraph = new SubGraph(centerNode, radius, drawSNP));

            time("Colorize", this::colorize);

            time("Drawing", () -> draw(gc));

            centerOnNodeId(center);
            highlightNode(center, Color.DARKORANGE);
        });
    }

    /**
     * Method to do the coloring of the to be drawn graph.
     */
    private void colorize() {
        subGraph.colorize();
    }

    /**
     * Fill the rectangles with the color.
     *
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
     *
     * @param nodes The nodes to highlight.
     * @param color The color to highlight with.
     */
    private void highlightNodes(Collection<DrawableNode> nodes, Color color) {
        for (DrawableNode drawNode : nodes) {
            highlightNode(drawNode, color);
        }
    }

    /**
     * Fill the rectangle with the color.
     *
     * @param nodeID the nodeID of the node to highlight.
     * @param color  the {@link Color} to highlight with.
     */
    private void highlightNode(int nodeID, Color color) {
        DrawableNode node = subGraph.getNodes().get(nodeID);
        highlightNode(node, color);
    }

    /**
     * Highlights a single node.
     *
     * @param node  {@link DrawableNode} to highlight.
     * @param color {@link Color} to color with.
     */
    private void highlightNode(DrawableNode node, Color color) {
        node.setStrokeColor(color);
        node.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        drawNode(canvas.getGraphicsContext2D(), node);
    }

    /**
     * Method to highlight a Link. Changes the stroke color of the Link.
     *
     * @param edge  {@link DrawableEdge} is the edge to highlight.
     * @param color {@link Color} is the color in which the Link node needs to highlight.
     */
    private void highlightEdge(DrawableEdge edge, Color color) {
        edge.setStrokeColor(color);
    }

    /**
     * Method to highlight a dummy node. Changes the stroke color of the node.
     *
     * @param node  {@link DrawableDummy} is the dummy node that needs highlighting.
     * @param color {@link Color} is the color in which the dummy node needs a highlight.
     */
    private void highlightDummyNode(DrawableDummy node, Color color) {
        node.setStrokeColor(color);
    }

    /**
     * Draws a edge on the location it has.
     *
     * @param gc     {@link GraphicsContext} is the GraphicsContext required to draw.
     * @param parent {@link DrawableNode} is the node to be draw from.
     * @param child  {@link DrawableNode} is the node to draw to.
     */
    private void drawEdge(GraphicsContext gc, DrawableNode parent, DrawableNode child) {
        DrawableEdge edge = new DrawableEdge(parent, child);

        edge.colorize(subGraph);

        gc.setLineWidth(edge.getStrokeWidth());
        gc.setStroke(edge.getStrokeColor());

        XYCoordinate startLocation = edge.getStartLocation();
        XYCoordinate endLayerLocation = new XYCoordinate(
                parent.getLeftBorderCenter().getX() + parent.getLayer().getWidth(),
                parent.getLeftBorderCenter().getY());
        XYCoordinate endLocation = edge.getEndLocation();

        gc.strokeLine(startLocation.getX(), startLocation.getY(), endLayerLocation.getX(), endLayerLocation.getY());
        gc.strokeLine(endLayerLocation.getX(), endLayerLocation.getY(), endLocation.getX(), endLocation.getY());
    }

    /**
     * Draws a node on the location it has.
     *
     * @param gc           {@link GraphicsContext} is the GraphicsContext required to draw.
     * @param drawableNode {@link DrawableNode} is the node to be drawn.
     */
    private void drawNode(GraphicsContext gc, DrawableNode drawableNode) {
        gc.setStroke(drawableNode.getStrokeColor());
        gc.setFill(drawableNode.getFillColor());
        gc.setLineWidth(drawableNode.getStrokeWidth());

        double width = drawableNode.getWidth();
        double height = drawableNode.getHeight();
        double locX = drawableNode.getLocation().getX();
        double locY = drawableNode.getLocation().getY();

        if (drawableNode instanceof DrawableSNP) {
            drawSNP(gc, (DrawableSNP) drawableNode);

        } else if (drawableNode instanceof DrawableDummy) {
            gc.strokeRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height);
            gc.fillRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height);

        } else {
            gc.strokeRoundRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height, archFactor, archFactor);
            gc.fillRoundRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height, archFactor, archFactor);
        }

    }

    /**
     * Draws an SNP node on the location it has.
     *
     * @param gc {@link GraphicsContext} is the GraphicsContext required to draw.
     * @param drawableSNP {@link DrawableSNP} is the node to be drawn.
     */
    private void drawSNP(GraphicsContext gc, DrawableSNP drawableSNP) {
        double width = drawableSNP.getWidth();
        double height = drawableSNP.getHeight();
        double locX = drawableSNP.getLocation().getX();
        double locY = drawableSNP.getLocation().getY();

        gc.save();

        gc.translate(drawableSNP.getCenter().getX(), drawableSNP.getCenter().getY());
        gc.rotate(45);
        gc.translate(-drawableSNP.getCenter().getX(), -drawableSNP.getCenter().getY());

        int size = drawableSNP.getMutations().size();
        int seqNumber = 0;
        gc.strokeRoundRect(locX, locY, width, height, archFactor, archFactor);

        for (DrawableSegment drawableSegment : drawableSNP.getMutations()) {
            String seqChar = drawableSegment.getSequence();
            switch (seqChar) {
                default:
                    throw new IllegalArgumentException("This is not a valid mutation.");
                case "A":
                    gc.setFill(Color.GREEN);
                    break;
                case "C":
                    gc.setFill(Color.BLUE);
                    break;
                case "G":
                    gc.setFill(Color.ORANGE);
                    break;
                case "T":
                    gc.setFill(Color.RED);
                    break;
                case "N":
                    gc.setFill(Color.WHITE);
            }
            gc.fillRect(locX + (width / size) * seqNumber, locY, width / size, height);
            seqNumber++;
        }
        gc.restore();
    }

    /**
     * Getter for the graph.
     *
     * @return - The graph
     */
    public GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Setter for the graph.
     *
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
     *
     * @param nodeId is the node to center on.
     */
    private void centerOnNodeId(int nodeId) {
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
     *
     * @param xDifference double with the value of the change in the X (horizontal) direction.
     * @param yDifference double with the value of the change in the Y (vertical) direction.
     */
    public void translate(double xDifference, double yDifference) {
        subGraph.translate(xDifference, yDifference);
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Zoom function for the nodes. Used to increase the size of the nodes instead of zooming in on the canvas itself.
     *
     * @param scale double with the value of the increase of the nodes. Value higher than 1 means that the node size
     *              decreases, value below 1 means that the node size increases.
     */
    public void zoom(double scale) {
        subGraph.zoom(scale);
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Draw method for the whole subgraph. Calls draw node and draw Edge for every node and edge.
     *
     * @param gc is the {@link GraphicsContext} required to draw.
     */
    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (clicked1 != null) {
            highlightNode(clicked1, Color.DARKTURQUOISE);
            clicked1.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNP1 != null) {
            highlightNode(clickedSNP1, Color.DARKTURQUOISE);
            clickedSNP1.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedShift != null) {
            highlightNode(clickedShift, Color.PURPLE);
            clickedShift.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNPShift != null) {
            highlightNode(clickedSNPShift, Color.PURPLE);
            clickedSNPShift.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clicked1 == clickedShift && clicked1 != null && clickedShift != null) {
            highlightNode(clicked1, Color.DARKCYAN);
            clicked1.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNP1 == clickedSNPShift && clickedSNP1 != null && clickedSNPShift != null) {
            highlightNode(clickedSNP1, Color.DARKCYAN);
            clickedSNP1.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }

        boolean didLoad = subGraph.checkDynamicLoad(0, canvas.getWidth());
        if (didLoad && highlightController != null) {
            highlightController.highlight();
        }

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            for (DrawableNode child : subGraph.getChildren(drawableNode)) {
                drawEdge(gc, drawableNode, child);
            }
        }

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            drawNode(gc, drawableNode);
        }
    }

    /**
     * Method to do highlighting based on a min and max amount of genomes in a node.
     *
     * @param min   The minimal amount of genomes
     * @param max   The maximal amount of genomes
     * @param color the {@link Color} in which the highlight has to be done.
     */
    public void highlightMinMax(int min, int max, Color color) {
        LinkedList<DrawableNode> drawNodeList = new LinkedList<>();

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            if (drawableNode != null && !(drawableNode instanceof DrawableDummy)) {
                int genomeCount = drawableNode.getGenomes().size();
                if (genomeCount >= min && genomeCount <= max) {
                    drawNodeList.add(drawableNode);
                }
            }
        }
        highlightNodes(drawNodeList, color);
    }

    /**
     * Resets the node highlighting to remove highlights.
     */
    void removeHighlight() {
        try {
            subGraph.forEach(node -> node.colorize(subGraph));
        } catch (NullPointerException n) {
            // Occurs when the subgraph is cleared upon opening another graph, nothing on the hand!
        }
        this.draw(canvas.getGraphicsContext2D());
    }


    /**
     * Highlights based on genomeID.
     *
     * @param genomeID the GenomeID to highlight on.
     * @param color the Color to put on the node.
     */
    public void highlightByGenome(int genomeID, Color color) {
        LinkedList<DrawableNode> drawNodeList = new LinkedList<>();
        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            Collection<Integer> genomes = drawableNode.getGenomes();
            for (int genome : genomes) {
                if (genome == genomeID && !(drawableNode instanceof DrawableDummy)) {
                    drawNodeList.add(drawableNode);
                }
            }
        }
        highlightNodes(drawNodeList, color);
    }

    /**
     * Sets if the glyph  snippets will be drawn or not.
     */
    void setSNP() {
        drawSNP = !drawSNP;
        resetClicked();
    }

    /**
     * Resets which nodes are clicked on.
     */
    void resetClicked() {
        clicked1 = null;
        clickedShift = null;
        clickedSNP1 = null;
        clickedSNPShift = null;
    }

    /**
     * Returns the node clicked on else returns null.
     *
     * @param x position horizontally where clicked
     * @param y position vertically where clicked
     * @return nodeClicked {@link DrawableNode} returns null if no node is clicked.
     */
    public Drawable onClick(double x, double y) {
        return subGraph.onClick(x, y);
    }

    /**
     * Method to highlight the node clicked on.
     *
     * @param segment      is the {@link DrawableSegment} clicked on.
     * @param snp          is the {@link DrawableSNP} clicked on.
     * @param shiftPressed boolean true if shift was pressed during the click.
     */
    public void highlightClicked(DrawableSegment segment, DrawableSNP snp, boolean shiftPressed) {
        if (shiftPressed) {
            if (clicked1 != null) {
                this.clicked1.colorize(subGraph);
            }
            if (clickedSNP1 != null) {
                this.clickedSNP1.colorize(subGraph);
            }
            this.clicked1 = segment;
            this.clickedSNP1 = snp;
            if (segment != null) {
                highlightNode(segment, Color.DARKTURQUOISE);
                segment.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
            if (snp != null) {
                highlightNode(snp, Color.DARKTURQUOISE);
                snp.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
        } else {
            if (clickedShift != null) {
                this.clickedShift.colorize(subGraph);
            }
            if (clickedSNPShift != null) {
                this.clickedSNPShift.colorize(subGraph);
            }
            this.clickedShift = segment;
            this.clickedSNPShift = snp;
            if (segment != null) {
                highlightNode(segment, Color.PURPLE);
                segment.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
            if (snp != null) {
                highlightNode(snp, Color.PURPLE);
                snp.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
        }
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Method to reset the zoomLevel.
     */
    public void resetZoom() {
        this.subGraph.setZoomLevel(1);
    }

    /**
     * Method to return the genomes in a given edge.
     *
     * @param edge the Drawable edge the check which genomes it contains.
     * @return Collection<Integer> of the genomes in the edge.
     */
    public Collection<Integer> getGenomesEdge(DrawableEdge edge) {
        Map<DrawableNode, Collection<Integer>> from = subGraph.getGenomes().get(edge.getStart().getParentSegment());
        if (from != null) {
            Collection<Integer> genomes = from.get(edge.getEnd().getChildSegment());
            return genomes;
        }
        return null;
    }

    public void setHighlightController(HighlightController highlightController) {
        this.highlightController = highlightController;
    }
}
