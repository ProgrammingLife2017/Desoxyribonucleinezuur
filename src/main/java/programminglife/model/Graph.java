package programminglife.model;

import java.util.Set;

/**
 * Interface for the graph.
 */
public interface Graph {
    /**
     * Getter for the ID.
     * @return String with the ID.
     */
    String getID();

    /**
     * Add a node to the graph.
     * @param node Node.
     */
    void addNode(Node node);

    /**
     * Add a node to the graph.
     * @param node Node.
     * @param children Set<Node> which contains the children.
     * @param parents Set<Node> which contains the parents.
     */
    void addNode(Node node, Set<Node> children, Set<Node> parents);

    /**
     * Replaces a node.
     * @param node Node.
     */
    void replaceNode(Node node);

    /**
     * Replaces a node.
     * @param node Node.
     * @param children Set<Node> of the children.
     * @param parents Set<Node> of the parents.
     */
    void replaceNode(Node node, Set<Node> children, Set<Node> parents);

    /**
     * Gives the size of a graph.
     * @return int of the size.
     */
    int size();

    /**
     * getter for the Children of a node.
     * @param node Node.
     * @return Set<Segment> of the children.
     */
    Set<Segment> getChildren(Node node);

    /**
     * getter for the Children of a node.
     * @param nodeID int.
     * @return Set<Segment> of the children.
     */
    Set<Segment> getChildren(int nodeID);

    /**
     * getter for the Parents of a node.
     * @param node Node.
     * @return Set<Segment> of the parents.
     */
    Set<Segment> getParents(Node node);

    /**
     * getter for the Parents of a node.
     * @param nodeID int.
     * @return Set<Segment> of the parents.
     */
    Set<Segment> getParents(int nodeID);

    /**
     * getter for the Genomes of a node.
     * @param node Node.
     * @return Set<Segment>.
     */
    Set<Genome> getGenomes(Node node);

    /**
     * Checks to see if the graph contains a certain node.
     * @param node Node.
     * @return boolean.
     */
    boolean contains(Node node);

    /**
     * Checks to see if the graph contains a certain node.
     * @param nodeID int.
     * @return boolean.
     */
    boolean contains(int nodeID);

    /**
     * Adds an edge between two nodes.
     * @param source Node of the source.
     * @param destination Node of the destination.
     */
    void addEdge(Node source, Node destination);
}
