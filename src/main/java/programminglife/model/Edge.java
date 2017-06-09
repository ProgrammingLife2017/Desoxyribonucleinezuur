package programminglife.model;

/**
 * An interface for an Edge between Nodes of type N.
 */
public interface Edge {

    /**
     * get all the Genomes that go through this Edge.
     * @return The genomes that go through this edge.
     */
    int[] getGenomes();

    /**
     * get the start of this edge.
     * @return The start of this edge.
     */
    Node getStart();

    /**
     * get the end of this edge.
     * @return The end of this edge.
     */
    Node getEnd();
}
