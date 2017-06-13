package programminglife.model.drawing;

import javafx.scene.paint.Color;

/**
 * Something that can be drawn on the screen.
 * A Drawable has a location, which is in the top left of the Drawable
 */
public interface Drawable {
    void setStrokeColor(Color color);

    void setStrokeWidth(double width);
}
