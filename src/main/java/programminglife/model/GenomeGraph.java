package programminglife.model;

import org.jetbrains.annotations.NotNull;
import org.mapdb.HTreeMap;

import java.util.*;

/**
 * Created by marti_000 on 25-4-2017.
 */
public class GenomeGraph implements Graph<Segment, Link> {
    private String id;
    private Set<Segment> rootNodes;

    /**
     * A list of nodes ordered by ID. Assumption: Nodes appear in GFA file in sequential order.
     */
    private HTreeMap<Integer, Segment> nodes;

    /**
     * Create a genomeGraph with name.
     * @param name name of the graph
     */
    public GenomeGraph(String name) {
        this(name, DataManager.getSegmentStorage(name));
    }

    /**
     * The constructor for a GenomeGraph.
     * @param id String id.
     * @param nodes A HTreeMap with ids mapped to Segments.
     */
    public GenomeGraph(String id, HTreeMap<Integer, Segment> nodes) {
        this.nodes = nodes;
        this.id = id;
        this.rootNodes = new HashSet<>();
    }

    /**
     * Add method for a node.
     * @param node Segment.
     * @return the previous node with this id.
     */
    public Segment addNode(Segment node) {
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
     * @return Segment.
     */
    public Segment getNode(int id) {
        Segment res = this.nodes.get(id);
        if (res != null) {
            return res;
        } else {
            throw new NoSuchElementException("There is no node with ID " + id);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Collection<Segment> getNodes() {
        return this.nodes.values();
    }

    /**
     * check whether this graph contains any of the Nodes in nodes.
     * This method short-circuits: as soon as a node is found that is in this graph, it returns true.
     * @param nodes the nodes to check for
     * @return true if this graph contains any of the nodes in nodes, false otherwise.
     */
    public boolean containsAny(Collection<Segment> nodes) {
        for (Segment node: nodes) {
            // check identifier instead of node because checking keys is faster than values.
            if (this.nodes.containsKey(node.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all root nodes (make sure to call {@link programminglife.parser.GraphParser#findRootNodes()} first.
     * @return a {@link Set} of root nodes of the {@link GenomeGraph}.
     */
    public Set<Segment> getRootNodes() {
        return rootNodes;
    }

    /**
     * Get the {@link GenomeGraph} ID.
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get the number of {@link Segment}s in the {@link GenomeGraph}.
     * @return the number of {@link Segment}s
     */
    public int size() {
        return this.getNodes().size();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public Iterator<Segment> iterator() {
        return this.getNodes().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAll(Collection<? extends Segment> nodes) {
        nodes.forEach(this::addNode);
    }
}
