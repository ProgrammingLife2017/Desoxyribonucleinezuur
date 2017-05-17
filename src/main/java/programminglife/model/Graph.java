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
     * The constructor for a Graph.
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
     * @return the previous node with this id.
     */
    public Node addNode(Node node) {
        this.rootNodes.removeAll(node.getChildren()); // any children of this node are no longer a root
        if (!this.containsAny(node.getParents())) {
            this.rootNodes.add(node); // this node is a root if none of its parents are in this graph
        }

        // add node
        return this.nodes.put(node.getIdentifier(), node);
    }

    /**
     * get method for a node.
     * @param id int.
     * @return Node.
     */
    public Node getNode(int id) {
        Node res = this.nodes.get(id);
        if (res != null) {
            return res;
        } else {
            throw new NoSuchElementException("There is no node with ID " + id);
        }
    }


    /**
     * get a collection of all nodes in this graph. This collection can be modified without changing this graph.
     * @return all nodes
     */
    public Collection<Node> getNodes() {
        return this.nodes.values();
    }

    /**
     * check whether this graph contains any of the Nodes in nodes.
     * This method short-circuits: as soon as a node is found that is in this graph, it returns true.
     * @param nodes the nodes to check for
     * @return true if this graph contains any of the nodes in nodes, false otherwise.
     */
    public boolean containsAny(Collection<Node> nodes) {
        for (Node node: nodes) {
            // check identifier instead of node because checking keys is faster than values.
            if (this.nodes.containsKey(node.getIdentifier())) {
                return true;
            }
        }
        return false;
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
