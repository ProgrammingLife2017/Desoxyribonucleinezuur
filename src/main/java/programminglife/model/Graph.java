package programminglife.model;

import java.util.*;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class Graph {
    private String id;
    private Set<Node> rootNodes;

    /**
     * A list of nodes ordered by ID. Assumption: Nodes appear in GFA file in sequential order.
     */
    private Map<Integer, Node> nodes;

    /**
     * The contructor for a Graph.
     * @param id String id.
     */
    public Graph(String id) {
        this.nodes = new HashMap<>(500000, 0.9f);
        this.id = id;
        this.rootNodes = new HashSet<>();
    }

    /**
     * Add method for a node.
     * @param node Node.
     * @return Node.
     */
    public Node addNode(Node node) {
        return this.nodes.put(node.getIdentifier(), node);
    }

    /**
     * get method for a node.
     * @param id int.
     * @return Node.
     */
    public Node getNode(int id) {
        if (this.nodes.containsKey(id)) {
            return this.nodes.get(id);
        } else {
            throw new NoSuchElementException("There is no node with ID " + id);
        }
    }

    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    /**
     * Get all root nodes (make sure to call {@link programminglife.parser.GraphParser#findRootNodes()} first.
     * @return a {@link Set} of root nodes of the {@link Graph}.
     */
    public Set<Node> getRootNodes() {
        return rootNodes;
    }

    /**
     * Get the {@link Graph} ID.
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the number of {@link Node}s in the {@link Graph}.
     * @return the number of {@link Node}s
     */
    public int size() {
        return this.getNodes().size();
    }
}
