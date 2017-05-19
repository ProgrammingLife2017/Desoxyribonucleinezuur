package programminglife.model;


import java.util.Collection;

/**
 * An Interface for graph structures, using dynamic {@link Node} and {@link Edge} implementations.
 * @param <N> an implementation of {@link Node} to use for node/vertex objects.
 * @param <E> an implementation of {@link Edge} to use for link/edge objects.
 */
public interface Graph<N extends Node<N>, E extends Edge<N>> extends Iterable<N> {
    /**
     * Add multiple {@link Node}s to the {@link Graph}.
     * @param nodes the {@link Collection} of {@link Node}s to add
     */
    void addAll(Collection<? extends N> nodes);

    /**
     * Get the {@link Node}s of this {@link Graph}.
     *
     * This {@link Collection} can be edited without influencing the {@link Graph}.
     * @return the {@link Collection} of {@link Node}s
     */
    Collection<? extends N> getNodes();

    int size();

    /**
     * @param node {@link Node} to check if it is contained in the graph.
     * @return boolean with the result of the contains.
     */
    boolean contains(Node node);
}
