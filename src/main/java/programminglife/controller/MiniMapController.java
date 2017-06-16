package programminglife.controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import programminglife.gui.controller.GuiController;

/**
 * Controller that shows a miniMap in the gui.
 */
public class MiniMapController {

    private GuiController guiController;
    private Canvas miniMap;
    private int size;

    private boolean visible = false;

    /**
     * Constructor for the miniMap.
     * @param miniMap Canvas of the miniMap to be used.
     * @param size int Size of the graph.
     */
    public MiniMapController(Canvas miniMap, int size) {
        this.miniMap = miniMap;
        miniMap.setVisible(visible);
        this.size = size;
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
    public void toggleVisibility() {
        visible = !visible;
        this.miniMap.setVisible(visible);
        if (visible) {
            drawMiniMap();
        }
    }

    /**
     * Shows the position of where you are in the graph (on the screen).
     * It does not handle panning as of now!
     * @param centerNode int of the centernode currently at.
     */
    public void showPosition(int centerNode) {
        GraphicsContext gc = miniMap.getGraphicsContext2D();
        gc.clearRect(0, 0, miniMap.getWidth(), miniMap.getHeight());
        drawMiniMap();
        gc.setFill(Color.RED);
        System.out.println(centerNode);
        System.out.println(size);
        System.out.println(miniMap.getWidth());
        gc.fillOval((centerNode / (double) size) * miniMap.getWidth(), 20, 10, 10);
    }

    /**
     * Sets the guicontroller for controlling the menu.
     * @param guiController The gui controller
     */
    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }
}
