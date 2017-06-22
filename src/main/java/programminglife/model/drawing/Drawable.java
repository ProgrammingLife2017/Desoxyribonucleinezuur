package programminglife.model.drawing;

import javafx.scene.paint.Color;
import java.util.Collection;


/**
 * Something that can be drawn on the screen.
 * A Drawable has a location, which is in the top left of the Drawable
 */
public interface Drawable {

    /**
     * Sets the stroke color of a Drawable.
     * @param color {@link Color} to set the stroke color to.
     */
    void setStrokeColor(Color color);

    /**
     * Sets the stroke width of a Drawable.
     * @param width double value of the width.
     */
    void setStrokeWidth(double width);

    /**
     * Get the genomes in/through this {@link Drawable}.
     * @return a {@link Collection} of genome IDs
     */
    Collection<Integer> getGenomes();

}
