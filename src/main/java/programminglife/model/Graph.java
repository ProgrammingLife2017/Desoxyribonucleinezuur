package programminglife.model;

import java.util.Collection;
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
     * @param children {@link Set} which contains the children.
     * @param parents {@link Set} which contains the parents.
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
     * @param children {@link Set} of the children.
     * @param parents {@link Set} of the parents.
     */
    void replaceNode(Node node, Set<Node> children, Set<Node> parents);

    /**
     * Gives the size of a graph.
     * @return int of the size.
     */
    int size();

    /**
     * getter for the Children of a node.
     * @param node the {@link Node} to get the children from.
     * @return {@link Set} of the children.
     */
    Set<Segment> getChildren(Node node);

    /**
     * getter for the Children of a node.
     * @param nodeID the node ID to get children from
     * @return {@link Set} of the children.
     */
    Set<Segment> getChildren(int nodeID);

    /**
     * getter for the Parents of a node.
     * @param node Node.
     * @return {@link Set} of the parents.
     */
    Set<Segment> getParents(Node node);

    /**
     * getter for the Parents of a node.
     * @param nodeID int.
     * @return {@link Set} of the parents.
     */
    Set<Segment> getParents(int nodeID);

    /**
     * getter for the Genomes of a node.
     * @param node Node.
     * @return {@link Set}.
     */
    Collection<Genome> getGenomes(Node node);

    /**
     * getter for the Genomes of a Link.
     * @param parent The parent of the link.
     * @param child The child of the link.
     * @return {@link Set}.
     */
    Collection<Genome> getGenomes(Node parent, Node child);

    /**
     * getter for a Link (via parent and child)
     * @param parent The parent of the link.
     * @param child The child of the link.
     * @return
     */
    Link getLink(Node parent, Node child);

    /**
     * Checks to see if the graph contains a certain node.
     * @param node the {@link Node} to check.
     * @return if the {@link Node} is in the {@link Graph}
     */
    boolean contains(Node node);

    /**
     * Checks to see if the graph contains a certain node.
     * @param nodeID node ID to check for if it is in the {@link Graph}
     * @return if the {@link Node} is in the {@link Graph}
     */
    boolean contains(int nodeID);

    /**
     * Adds an edge between two nodes.
     * @param source Node of the source.
     * @param destination Node of the destination.
     */
    void addEdge(Node source, Node destination);
}
