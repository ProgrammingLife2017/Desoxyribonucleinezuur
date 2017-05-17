package programminglife.model;

import java.util.Collection;

/**
 * An interface for an Edge between Nodes of type N.
 * @param <N> the type of Nodes this Edge connects
 */
public interface Edge<N extends Node<N>> {

    /**
     * get all the Genomes that go through this Edge.
     * @return The genomes that go through this edge.
     */
    Collection<Genome> getGenomes();

    /**
     * get the start of this edge.
     * @return The start of this edge.
     */
    N getStart();

    /**
     * get the end of this edge.
     * @return The end of this edge.
     */
    N getEnd();
}
