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
     * Get all children of this {@link Node}.
     * @return all children
     */
    Collection<N> getChildren();

    /**
     * Get all parent {@link Node}s of this {@link Node}.
     * @return all parents
     */
    Collection<N> getParents();

    /**
     * Get all {@link Genome}s this {@link Node} is part of.
     * @return all {@link Genome}s
     */
    Collection<Genome> getGenomes();

    /**
     * Get the {@link Collection} of bookmarks for this {@link Genome}.
     * @return all bookmarks
     */
    default Collection<Object> getBookmarks() {
        // TODO create a data structure to store bookmarks/annotation
        throw new NotImplementedException("Node#getBookmarks() is not yet implemented");
    }
}
