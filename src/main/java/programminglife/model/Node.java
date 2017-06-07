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
     * Returns the childEdges {@link Collection} of the node {@link Node}.
     * @return childEdges {@link Collection} are the edges to the children of the {@link Node}.
     */
    Collection<? extends Edge> getChildEdges();

    /**
     * Returns the parentEdges {@link Collection} of the node {@link Node}.
     * @return childEdges {@link Collection} are the edges to the children of the {@link Node}.
     */
    Collection<? extends Edge> getParentEdges();

    /**
     * Returns the children {@link Collection} of the node {@link Node}.
     * @return children {@link Collection} are the children of the node {@link Node}.
     */
    Collection<? extends Node> getChildren();

    /**
     * Returns the parents {@link Collection} of the node {@link Node}.
     * @return parents {@link Collection} are the parents of the node {@link Node}.
     */
    Collection<? extends Node> getParents();

    /**
     * getter for genomes.
     * @return genomes {@link Collection}.
     */
    int[] getGenomes();

    /**
     * Getter for the sequence of this node.
     * @return a {@link String}
     */
    String getSequence();

    /**
     * getter for sequencelength.
     * @return int with length of the sequence.
     */
    int getSequenceLength();

    /**
     * Getter for Link.
     * @param node {@link Node} the child node of this node for this Link.
     * @return The {@link Link} of the {@link Node}
     */
    Link getLink(Node node);

    /**
     * Setter for the sequence.
     * @param sequence {@link String} of the sequence.
     */
    void setSequence(String sequence);
}
