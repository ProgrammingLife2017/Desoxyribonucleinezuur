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

    /**
     * Returns the childEdges {@link Collection<? extends Edge>} of the node {@link Node}.
     * @return childEdges {@link Collection<? extends Edge>} are the edges to the children of the {@link Node}.
     */
    public Collection<? extends Edge> getChildEdges();

    /**
     * Returns the parentEdges {@link Collection<? extends Edge>} of the node {@link Node}.
     * @return childEdges {@link Collection<? extends Edge>} are the edges to the children of the {@link Node}.
     */
    public Collection<? extends Edge> getParentEdges();

    /**
     * Returns the children {@link Collection<? extends Node>} of the node {@link Node}.
     * @return children {@link Collection<? extends Node>} are the children of the node {@link Node}.
     */
    public Collection<? extends Node> getChildren();

    /**
     * Returns the parents {@link Collection<? extends Node>} of the node {@link Node}.
     * @return parents {@link Collection<? extends Node>} are the parents of the node {@link Node}.
     */
    public Collection<? extends Node> getParents();
    public Collection<Genome> getGenomes();
    public int getSequenceLength();
    void setSequence(String sequence);

    String getSequence();
}
