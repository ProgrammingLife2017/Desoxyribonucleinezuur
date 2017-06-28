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

    private static final int SPLIT_PANEL_WIDTH = 15;

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
        gc.setFill(Color.BURLYWOOD);
        gc.fillRect(0, 0, miniMap.getWidth(), 50);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(0, 49, miniMap.getWidth(), 49);
        gc.strokeLine(0, 1, miniMap.getWidth(), 1);

        for (int i = 0; i <= 10; i++) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeLine(i * miniMap.getWidth() / 10, 50, i * miniMap.getWidth() / 10, 0);
        }
        for (int i = 0; i < 10; i++) {
            gc.setFill(Color.BLACK);
            gc.fillText("Node: " + String.valueOf(i * graphController.getGraph().size() / 10),
                    i * miniMap.getWidth() / 10, 45);
        }
    }

    /**
     * Toggle the visibility of the MiniMap.
     *
     * @param centerNodeId node to be centered on.
     */
    void toggleVisibility(int centerNodeId) {
        visible = !visible;
        this.miniMap.setVisible(visible);
        if (visible) {
            drawMiniMap();
            showPosition(centerNodeId);
        }
    }

    /**
     * Method to handle the event of the mouse.
     *
     * @param event MouseEvent to be processed.
     */
    private void handleMouse(MouseEvent event) {
        double locX = event.getSceneX() - this.guiController.getAnchorLeftControlPanel().getWidth() - SPLIT_PANEL_WIDTH;
        double ratio = locX / miniMap.getBoundsInParent().getMaxX();
        int centerNodeId = (int) Math.ceil(graphController.getGraph().size() * ratio);
        guiController.setText(centerNodeId);
        guiController.draw();
    }

    /**
     * Method to initialize the mouseEvent.
     */
    private void initClick() {
        miniMap.setOnMousePressed(this::handleMouse);
    }

    /**
     * Shows the position of where you are in the graph (on the screen).
     * It does not handle panning as of now!
     *
     * @param centerNode int of the centernode currently at.
     */
    void showPosition(int centerNode) {
        GraphicsContext gc = miniMap.getGraphicsContext2D();
        gc.clearRect(0, 0, miniMap.getWidth(), miniMap.getHeight());
        drawMiniMap();
        gc.setFill(Color.RED);
        gc.fillOval((centerNode / (double) size) * miniMap.getWidth(), 20, 10, 10);
    }

    void setGraphController(GraphController graphController) {
        this.graphController = graphController;
    }

    void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
}
