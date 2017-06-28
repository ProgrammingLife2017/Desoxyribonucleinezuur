package programminglife.gui.controller;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Controller that shows a MiniMap in the gui.
 */
class MiniMapController {

    private final Canvas miniMap;
    private final int size;

    private boolean visible = false;
    private GraphController graphController;
    private GuiController guiController;

    private final static int SPLIT_PANEL_WIDTH = 15;

    /**
     * Constructor for the miniMap.
     *
     * @param miniMap Canvas of the miniMap to be used.
     * @param size    int Size of the graph.
     */
    MiniMapController(Canvas miniMap, int size) {
        this.miniMap = miniMap;
        Platform.runLater(this.miniMap::toFront);
        miniMap.setVisible(visible);
        this.size = size;
        this.initClick();
    }

    /**
     * Draws the MiniMap on the screen.
     */
    private void drawMiniMap() {
        GraphicsContext gc = miniMap.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, miniMap.getWidth(), 50);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(0, 25, miniMap.getWidth(), 25);
    }

    /**
     * Toggle the visibility of the MiniMap.
     */
    public void toggleVisibility(int centerNodeId) {
        visible = !visible;
        this.miniMap.setVisible(visible);
        if (visible) {
            drawMiniMap();
            showPosition(centerNodeId);
        }
    }

    public void handleMouse(MouseEvent event) {
        double locX = event.getSceneX() - this.guiController.anchorLeftControlPanel.getWidth() - SPLIT_PANEL_WIDTH;
        double ratio = locX / miniMap.getBoundsInParent().getMaxX();
        int centerNodeId = (int) Math.ceil(graphController.getGraph().size() * ratio);
        System.out.println("clicked");
        Platform.runLater(() -> graphController.draw(centerNodeId, 10));
    }

    public void initClick() {
        miniMap.setOnMousePressed(this::handleMouse);
    }

    /**
     * Shows the position of where you are in the graph (on the screen).
     * It does not handle panning as of now!
     *
     * @param centerNode int of the centernode currently at.
     */
    public void showPosition(int centerNode) {
        GraphicsContext gc = miniMap.getGraphicsContext2D();
        gc.clearRect(0, 0, miniMap.getWidth(), miniMap.getHeight());
        drawMiniMap();
        gc.setFill(Color.RED);
        gc.fillOval((centerNode / (double) size) * miniMap.getWidth(), 20, 10, 10);
    }

    public void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
}
