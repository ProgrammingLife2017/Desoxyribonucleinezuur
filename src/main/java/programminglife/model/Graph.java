package programminglife.model;

import java.util.Collection;

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
     * @param nodeID the ID of the node.
     */
    void addNode(int nodeID);

    /**
     * Add a node to the graph.
     * @param nodeID the ID of the node.
     * @param children int[] which contains the children.
     * @param parents int[] which contains the parents.
     */
    void addNode(int nodeID, int[] children, int[] parents);

    /**
     * Replaces a node.
     * @param nodeID the ID of the node.
     */
    void replaceNode(int nodeID);

    /**
     * Replaces a node.
     * @param nodeID the ID of the node.
     * @param children int[] of the children.
     * @param parents int[] of the parents.
     */
    void replaceNode(int nodeID, int[] children, int[] parents);

    /**
     * Gives the size of a graph.
     * @return int of the size.
     */
    int size();

    /**
     * getter for the Children of a node.
     * @param node the {@link Node} to get the children from.
     * @return int[] of the children.
     */
    int[] getChildIDs(Node node);

    /**
     * getter for the Children of a node.
     * @param nodeID the node ID to get children from
     * @return int[] of the children.
     */
    int[] getChildIDs(int nodeID);

    /**
     * getter for the Parents of a node.
     * @param node Node.
     * @return int[] of the parents.
     */
    int[] getParentIDs(Node node);

    /**
     * getter for the Parents of a node.
     * @param node The node of which the parents are returned.
     * @return int[] of the parents.
     */
    Collection<Node> getParents(Node node);

    /**
     * getter for the Parents of a node.
     * @param node The node of which the children are returned.
     * @return int[] of the parents.
     */
    Collection<Node> getChildren(Node node);

    /**
     * getter for a Link (via parent and child).
     * @param parent The parent of the link.
     * @param child The child of the link.
     * @return The link between parent and child.
     */
    Link getLink(Node parent, Node child);

    /**
     * getter for the genomes of a node.
     * @param node Node.
     * @return int[] of the genomes.
     */
    int[] getGenomes(Node node);

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
     * @param sourceID Node of the source.
     * @param destinationID Node of the destination.
     */
    void addEdge(int sourceID, int destinationID);
}
