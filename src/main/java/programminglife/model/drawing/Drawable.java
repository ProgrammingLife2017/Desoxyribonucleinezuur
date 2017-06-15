package programminglife.model.drawing;

import java.util.Collection;

/**
 * Something that can be drawn on the screen.
 * A Drawable has a location, which is in the top left of the Drawable
 */
public interface Drawable {
    Collection<Integer> getGenomes();
}
