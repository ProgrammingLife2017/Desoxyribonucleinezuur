package programminglife.model.drawing;

import java.util.Collection;

/**
 * Something that can be drawn on the screen.
 * A Drawable has a location, which is in the top left of the Drawable
 */
public interface Drawable {
    /**
     * Get the genomes in/through this {@link Drawable}.
     * @return a {@link Collection} of genome IDs
     */
    Collection<Integer> getGenomes();
}
