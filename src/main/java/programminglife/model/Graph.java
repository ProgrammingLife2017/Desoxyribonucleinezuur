package programminglife.model;

import java.util.Set;

/**
 * Interface for the graph.
 */
public interface Graph {
    /**
     * Getter for the ID.
     * @return String.
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
     * @param children Set<Node>.
     * @param parents Set<Node>.
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
     * @param children Set<Node>
     * @param parents Set<Node>
     */
    void replaceNode(Node node, Set<Node> children, Set<Node> parents);

    /**
     * Gives the size of a graph.
     * @return int.
     */
    int size();

    /**
     * getter for the Children of a node.
     * @param node Node.
     * @return Set<Segment>
     */
    Set<Segment> getChildren(Node node);

    /**
     * getter for the Children of a node.
     * @param nodeID int.
     * @return Set<Segment>
     */
    Set<Segment> getChildren(int nodeID);

    /**
     * getter for the Parents of a node.
     * @param node Node.
     * @return Set<Segment>
     */
    Set<Segment> getParents(Node node);

    /**
     * getter for the Parents of a node.
     * @param nodeID int.
     * @return Set<Segment>
     */
    Set<Segment> getParents(int nodeID);

    /**
     * getter for the Genomes of a node.
     * @param node Node.
     * @return Set<Segment>
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
     * @param source Node.
     * @param destination Node.
     */
    void addEdge(Node source, Node destination);
}
