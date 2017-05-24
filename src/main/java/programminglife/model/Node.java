package programminglife.model;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;

/**
 * An Interface for node objects.
 * @param <N> the implementation of {@link Node} to use
 */
public interface Node<N extends Node<N>> {
    /**
     * Getter for the id.
     * @return int.
     */
    int getIdentifier();

    /**
     * Get the {@link Collection} of bookmarks for this {@link Genome}.
     * @return all bookmarks
     */
    default Collection<Object> getBookmarks() {
        // TODO create a data structure to store bookmarks/annotation
        throw new NotImplementedException("Node#getBookmarks() is not yet implemented");
    }

    /**
     * Getter for the sequence of a node.
     * @return String sequence.
     */
    String getSequence();

    /**
     * Sets the sequence of a node.
     * @param sequence String.
     */
    void setSequence(String sequence);
}
