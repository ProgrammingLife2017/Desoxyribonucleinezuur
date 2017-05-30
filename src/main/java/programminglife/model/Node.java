package programminglife.model;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;

/**
 * An Interface for node objects.
 */
public interface Node {
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


    public Collection<? extends Edge> getChildEdges();
    public Collection<? extends Edge> getParentEdges();

    public Collection<? extends Node> getChildren();
    public Collection<? extends Node> getParents();

    public Collection<Genome> getGenomes();
}
