package programminglife.gui.controller;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import programminglife.ProgrammingLife;
import programminglife.gui.ResizableCanvas;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import programminglife.model.drawing.*;
import programminglife.utility.Console;

import java.util.*;
import java.util.stream.Collectors;

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

    private DrawableSegment clicked;
    private DrawableSegment clickedShift;
    private DrawableSNP clickedSNP;
    private DrawableSNP clickedSNPShift;
    private final Map<DrawableNode, List<Color>> nodeGenomeList;

    private int centerNodeInt;
    private boolean drawSNP = false;

    private HighlightController highlightController;
    private MiniMapController miniMapController;
    private GuiController guiController;

    /**
     * Initialize controller object.
     *
     * @param canvas the {@link Canvas} to draw in
     */
    GraphController(ResizableCanvas canvas) {
        this.graph = null;
        this.canvas = canvas;
        this.highlightController = null;
        this.nodeGenomeList = new HashMap<>();
    }

    int getCenterNodeInt() {
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
    void draw(int center, int radius) {
        time("Total drawing", () -> {
            DrawableSegment centerNode = new DrawableSegment(graph, center, 1);
            centerNodeInt = centerNode.getIdentifier();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            time("Find subgraph", () -> subGraph = new SubGraph(centerNode, radius, drawSNP));

            time("Colorize", this::colorize);

            time("Drawing", () -> draw(gc));

            int centerId = centerOnNodeId(center);
            highlightCenterNode(centerId, Color.DARKORANGE);
        });
    }

    /**
     * Method to do the coloring of the to be drawn graph.
     */
    private void colorize() {
        subGraph.colorize();
    }

    /**
     * Method to highlight a collection of nodes.
     *
     * @param nodes The nodes to highlight.
     * @param color The color to highlight with.
     */
    private void highlightNodes(Collection<DrawableNode> nodes, Color color) {
        for (DrawableNode drawNode : nodes) {
            highlightNode(drawNode, color, false);
        }
    }

    /**
     * Fill the rectangle with the color.
     *
     * @param nodeID the nodeID of the node to highlight.
     * @param color  the {@link Color} to highlight with.
     */
    private void highlightCenterNode(int nodeID, Color color) {
        DrawableNode node = subGraph.getNodes().get(nodeID);
        highlightNode(node, color, true);
    }

    /**
     * Highlights a single node.
     *
     * @param node  {@link DrawableNode} to highlight.
     * @param color {@link Color} to color with.
     * @param clickedOn boolean to check if the node was clicked on.
     */
    private void highlightNode(DrawableNode node, Color color, Boolean clickedOn) {
        if (clickedOn) {
            node.setStrokeColor(color);
        }
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

        Color[] genomeColors = highlightController.getGenomeColors();

        Collection<Integer> genomesEdge = this.getGenomesEdge(edge);
        Collection<Integer> highlightedGenomes = highlightController.getSelectedGenomes();
        List<Color> genomesToDraw = null;
        if (genomesEdge != null) {
            genomesToDraw = highlightedGenomes.stream()
                    .filter(genomesEdge::contains)
                    .map(id -> genomeColors[id])
                    .collect(Collectors.toList());
        }

        edge.colorize(subGraph);

        gc.setLineWidth(edge.getStrokeWidth());
        gc.setStroke(edge.getStrokeColor());
        if (ProgrammingLife.getShowCSS()) {
            gc.setStroke(Color.WHITE);
        }

        XYCoordinate startLocation = edge.getStartLocation();

        XYCoordinate endLayerLocation = new XYCoordinate(
                parent.getLeftBorderCenter().getX() + parent.getLayer().getWidth(),
                parent.getLeftBorderCenter().getY());

        XYCoordinate endLocation = edge.getEndLocation();

        if (genomesToDraw == null || genomesToDraw.size() == 0) {
            gc.strokeLine(startLocation.getX(), startLocation.getY(), endLayerLocation.getX(), endLayerLocation.getY());
            gc.strokeLine(endLayerLocation.getX(), endLayerLocation.getY(), endLocation.getX(), endLocation.getY());
        } else {
            int seqNumber = 0;
            int numberOfGenomes = genomesToDraw.size();
            double genomeHeight = edge.getStrokeWidth() / numberOfGenomes;
            startLocation.setY(startLocation.getY() - (genomeHeight * numberOfGenomes / 2));
            endLayerLocation.setY(endLayerLocation.getY() - (genomeHeight * numberOfGenomes / 2));
            endLocation.setY(endLocation.getY() - (genomeHeight * numberOfGenomes / 2));

            gc.save();
            gc.setLineWidth(genomeHeight);

            for (Color color : genomesToDraw) {
                gc.setStroke(color);
                gc.strokeLine(startLocation.getX(), startLocation.getY() + genomeHeight * seqNumber,
                        endLayerLocation.getX(), endLayerLocation.getY() + genomeHeight * seqNumber);

                gc.strokeLine(endLayerLocation.getX(), endLayerLocation.getY() + genomeHeight * seqNumber,
                        endLocation.getX(), endLocation.getY() + genomeHeight * seqNumber);

                seqNumber++;
            }
            gc.restore();
        }

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
            Color[] genomeColors = highlightController.getGenomeColors();
            Collection<Integer> genomesDummy = drawableNode.getGenomes();
            Collection<Integer> highlightedGenomes = highlightController.getSelectedGenomes();
            List<Color> genomesToDraw = null;
            if (genomesDummy != null) {
                genomesToDraw = highlightedGenomes.stream()
                        .filter(genomesDummy::contains)
                        .map(id -> genomeColors[id])
                        .collect(Collectors.toList());
            }
            if (ProgrammingLife.getShowCSS()) {
                gc.setStroke(Color.WHITE);
            }
            if (genomesToDraw != null && genomesToDraw.size() > 0) {
                int seqNumber = 0;
                double genomeHeight = drawableNode.getStrokeWidth() / genomesToDraw.size();

                XYCoordinate location = drawableNode.getLocation();

                gc.save();
                gc.setLineWidth(genomeHeight);
                for (Color color : genomesToDraw) {
                    gc.setStroke(color);
                    gc.strokeLine(location.getX(), location.getY() + genomeHeight * seqNumber
                                    - (genomeHeight * genomesToDraw.size() / 2),
                            location.getX() + width, location.getY() + genomeHeight * seqNumber
                                    - (genomeHeight * genomesToDraw.size() / 2));

                    seqNumber++;
                }
                gc.restore();
            } else {
                gc.strokeRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height);
                gc.fillRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height);
            }

        } else if (!nodeGenomeList.containsKey(drawableNode)) {
            gc.strokeRoundRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height, archFactor, archFactor);
            gc.fillRoundRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height, archFactor, archFactor);
        } else {
            int seqNumber = 0;
            int numberOfGenomes = nodeGenomeList.get(drawableNode).size();
            double genomeHeight = height / numberOfGenomes;

            gc.strokeRoundRect(drawableNode.getLeftBorderCenter().getX(), locY, width, height, archFactor, archFactor);
            gc.save();

            for (Color color : nodeGenomeList.get(drawableNode)) {
                gc.setFill(color);
                gc.fillRect(locX, locY + genomeHeight * seqNumber, width, genomeHeight);
                seqNumber++;
            }
            gc.restore();
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
    void clear() {
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
    private int centerOnNodeId(int nodeId) {
        DrawableNode drawableCenterNode = subGraph.getNodes().get(nodeId);
        int centerId = nodeId;

        Bounds bounds = canvas.getParent().getLayoutBounds();
        double boundsHeight = bounds.getHeight();
        double boundsWidth = bounds.getWidth();

        double xCoordinate;
        if (drawableCenterNode != null) {
            xCoordinate = drawableCenterNode.getCenter().getX();
        } else {
            if (graph.getChildIDs(nodeId).length > 0) {
                centerId = graph.getChildIDs(nodeId)[0];
            } else {
                centerId = graph.getParentIDs(nodeId)[0];
            }
            xCoordinate = subGraph.getNodes().get(centerId).getCenter().getX();
        }

        locationCenterY = boundsHeight / 4;
        locationCenterX = boundsWidth / 2 - xCoordinate;

        translate(locationCenterX, locationCenterY);
        return centerId;
    }

    /**
     * Translate function for the nodes. Used to change the location of nodes instead of mocing the canvas.
     *
     * @param xDifference double with the value of the change in the X (horizontal) direction.
     * @param yDifference double with the value of the change in the Y (vertical) direction.
     */
    void translate(double xDifference, double yDifference) {
        subGraph.translate(xDifference, yDifference);
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Zoom function for the nodes. Used to increase the size of the nodes instead of zooming in on the canvas itself.
     *
     * @param scale double with the value of the increase of the nodes. Value higher than 1 means that the node size
     *              decreases, value below 1 means that the node size increases.
     */
    void zoom(double scale) {
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

        if (clicked != null) {
            highlightNode(clicked, Color.CYAN, true);
            clicked.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNP != null) {
            highlightNode(clickedSNP, Color.CYAN, true);
            clickedSNP.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedShift != null) {
            highlightNode(clickedShift, Color.CYAN, true);
            clickedShift.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNPShift != null) {
            highlightNode(clickedSNPShift, Color.CYAN, true);
            clickedSNPShift.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clicked == clickedShift && clicked != null && clickedShift != null) {
            highlightNode(clicked, Color.DARKCYAN, true);
            clicked.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }
        if (clickedSNP == clickedSNPShift && clickedSNP != null && clickedSNPShift != null) {
            highlightNode(clickedSNP, Color.DARKCYAN, true);
            clickedSNP.setStrokeWidth(5.0 * subGraph.getZoomLevel());
        }

        boolean didLoad = subGraph.checkDynamicLoad(0, canvas.getWidth());
        Bounds bounds = canvas.getParent().getLayoutBounds();
        double centerCanvasX = bounds.getWidth() / 2;
        this.setCenterNode(subGraph.updateCenterNode(centerCanvasX, centerNodeInt));
        this.guiController.setText(this.centerNodeInt);
        this.miniMapController.showPosition(this.centerNodeInt);

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
    void highlightMinMax(int min, int max, Color color) {
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
            nodeGenomeList.clear();
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
    void highlightByGenome(int genomeID, Color color) {
        LinkedList<DrawableNode> drawNodeList = new LinkedList<>();

        for (DrawableNode drawableNode : subGraph.getNodes().values()) {
            Collection<Integer> genomes = drawableNode.getGenomes();
            List<Color> listColor = new LinkedList<>();
            for (int genome : genomes) {
                if (genome == genomeID && !(drawableNode instanceof DrawableDummy)) {
                    if (nodeGenomeList.containsKey(drawableNode) && drawableNode.getGenomes().contains(genome)) {
                        listColor = nodeGenomeList.get(drawableNode);
                    }
                    drawNodeList.add(drawableNode);
                    if (!listColor.contains(color)) {
                        listColor.add(color);
                    }
                    nodeGenomeList.put(drawableNode, listColor);
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
        clicked = null;
        clickedShift = null;
        clickedSNP = null;
        clickedSNPShift = null;
    }

    /**
     * Returns the node clicked on else returns null.
     *
     * @param x position horizontally where clicked
     * @param y position vertically where clicked
     * @return nodeClicked {@link DrawableNode} returns null if no node is clicked.
     */
    Drawable onClick(double x, double y) {
        return subGraph.onClick(x, y);
    }

    /**
     * Method to highlight the node clicked on.
     *
     * @param segment      is the {@link DrawableSegment} clicked on.
     * @param snp          is the {@link DrawableSNP} clicked on.
     * @param shiftPressed boolean true if shift was pressed during the click.
     */
    void highlightClicked(DrawableSegment segment, DrawableSNP snp, boolean shiftPressed) {
        if (shiftPressed) {
            if (clicked != null) {
                this.clicked.colorize(subGraph);
            }
            if (clickedSNP != null) {
                this.clickedSNP.colorize(subGraph);
            }
            this.clicked = segment;
            this.clickedSNP = snp;

            if (segment != null) {
                highlightNode(segment, Color.CYAN, true);
                segment.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
            if (snp != null) {
                highlightNode(snp, Color.CYAN, true);
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
                highlightNode(segment, Color.CYAN, true);
                segment.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
            if (snp != null) {
                highlightNode(snp, Color.CYAN, true);
                snp.setStrokeWidth(5.0 * subGraph.getZoomLevel()); //Correct thickness when zoomed
            }
        }
        draw(canvas.getGraphicsContext2D());
    }

    /**
     * Method to reset the zoomLevel.
     */
    void resetZoom() {
        this.subGraph.setZoomLevel(1);
    }

    /**
     * Method to return the genomes in a given edge.
     *
     * @param edge the Drawable edge the check which genomes it contains.
     * @return Collection<Integer> of the genomes in the edge.
     */
    Collection<Integer> getGenomesEdge(DrawableEdge edge) {
        Map<DrawableNode, Collection<Integer>> from = subGraph.getGenomes().get(edge.getStart().getParentSegment());
        if (from != null) {
            return from.get(edge.getEnd().getChildSegment());
        }
        return null;
    }

    /**
     * Method to return the segments in a given edge.
     *
     * @param node the Drawable segment the check which parent segments it contains.
     * @return Collection<Integer> of the parents segments in the node.
     */
    Collection<DrawableNode> getParentSegments(DrawableSegment node) {
        return subGraph.getParentSegments(node);
    }

    /**
     * Method to return the segments in a given edge.
     *
     * @param node the Drawable segment the check which child segments it contains.
     * @return Collection<Integer> of the child segments in the node.
     */
    Collection<DrawableNode> getChildSegments(DrawableSegment node) {
        return subGraph.getChildSegments(node);
    }

    /**
     * Setter for the highlight controller.
     *
     * @param highlightController the controller to be set.
     */
    void setHighlightController(HighlightController highlightController) {
        this.highlightController = highlightController;
    }

    private void setCenterNode(int id) {
        this.centerNodeInt = id;
    }

    void setMiniMapController(MiniMapController miniMapController) {
        this.miniMapController = miniMapController;
    }

    void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
}
