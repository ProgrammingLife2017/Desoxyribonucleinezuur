package programminglife.model;

import java.util.*;

/**
 * Created by Ivo on 2017-05-11.
 * This is a SubGraph, which means that not all children or parents of a
 * Node in this graph need to be within this graph.
 * TODO: make this class thread safe (because it is currently absolutely not.
 */
public class SubGraph implements Iterable<Node> {
    /**
     * This is the first sentence, which ends in a period, because checkstyle.
     * Invariants:
     *  - does not contain null
     *  - for each key-value pair, key == value.id
     */
    private Map<Integer, Node> nodes;

    /**
     * This is the first sentence, which ends in a period, because checkstyle.
     * Invariants
     *  - does not contain null
     *  - all rootNodes must be in nodes
     *  - rootNodes do not contain any parents in nodes
     *    (for each Node n in rootNotes, for each parent p of n, p is not in nodes)
     */
    private Set<Node> rootNodes;

    /**
     * This is the first sentence, which ends in a period, because checkstyle.
     * Invariants
     *  - does not contain null
     *  - all endNodes must be in nodes
     *  - endNodes do not contain any children in nodes
     *    (for each Node n in endNotes, for each child c of n, c is not in nodes)
     */
    private Set<Node> endNodes;

    /**
     * Nullary constructor: initializes without any Nodes.
     */
    public SubGraph() {
        this(new ArrayList<>(0));
    }

    /**
     * Create a Graph with Nodes nodes. If any of the Nodes has the same id,
     * the last one (as defined by the iterator for the collection) will be taken.
     * @param nodes Nodes with which this SubGraph will be instantiated.
     */
    public SubGraph(Collection<Node> nodes) {
        this.nodes = new HashMap<>();
        this.rootNodes = new HashSet<>();
        this.endNodes = new HashSet<>();

        // replaceAll since nodes is definitely empty,
        // and it saves a check that the node did not exist yet.
        this.replaceAll(nodes);
    }

    /**
     * Create a SubGraph with centerNode and all Nodes radius away from it.
     * @param centerNode The center (first) node
     * @param radius the number of steps that are taken before stopping.
     */
    public SubGraph(Node centerNode, int radius) {
        assert (centerNode != null);
        assert (radius >= 0);

        this.nodes = new HashMap<>();
        this.rootNodes = new HashSet<>();
        this.endNodes = new HashSet<>();

        addNodes(centerNode, radius);
    }

    /**
     * Create a SubGraph from a graph. The SubGraph will contain the same Nodes as the Graph.
     * Mostly for testing.
     * @param graph Graph to create this SubGraph from.
     */
    public SubGraph(Graph graph) {
        this.addAll(graph.getNodes());
    }

    /**
     * Find and add all nodes within  a certain radius from the centerNode.
     * This means finding all nodes that can be reached within radius steps from the centerNode.
     * @param node the node to start searching from
     * @param radius the radius, i.e. the number of steps taken before stopping
     */
    private void addNodes(Node node, int radius) {
        assert (radius >= 0);
        assert (node != null);

        // base case: node already added or radius is 0
        boolean bool1 = radius == 0;
        boolean bool2 = this.contains(node);
        if (bool1 || bool2) {
            return;
        }

        this.nodes.put(node.getIdentifier(), node);

        // add all children
        for (Node child : node.getChildren()) {
            addNodes(child, radius - 1);
        }

        // add all parents
        for (Node parent : node.getParents()) {
            addNodes(parent, radius - 1);
        }
    }

    /**
     * Add a collection of Nodes to this Graph.<br>
     * Because this uses the same interface as {@link #addNode(Node) AddNode}, this method will
     * throw an Exception when one of the nodes already exists.
     * This also means that the Collection cannot contain duplicates.<br>
     * If this happens, all Nodes before are inserted, but all Nodes after (including the duplicate Node) not.
     * If the iterator for the Collection does not return Nodes in a deterministic order,
     * the state of the SubGraph is essentially undefined.
     * You can partly fix it by calling {@link #removeAll(Collection<Node>) removeAll},
     * but it will remove any duplicates.
     * If you don't want this, use {@link #replaceAll(Collection<Node>) replaceAll} instead.
     * (That can also be used when you are absolutely certain that no nodes already exist,
     * which would be faster, as it skips that check then)<br>
     * Functionally the same as <br>
     * {@code for (Node n : nodes) {
     *      addNode(n);
     * }}<br>
     * except that this is optimized.
     * @param nodes The nodes to add.
     */
    public void addAll(Collection<Node> nodes) {
        // TODO: add approximate check for whether it would be faster to use AddNodeNoUpdate
        // and then recalculate roots and ends afterwards, or to just use addNode repeatedly.
        // This would mostly involve some computation on
        // the size of nodes and this.nodes, this.rootNodes and this.endNodes

        for (Node node : nodes) {
            this.addNodeNoUpdate(node);
        }

        recalculateRootsAndEnds();
    }

    /**
     * Replace Nodes in this graph with the Nodes in nodes.
     * Functionally the same as <br>
     * {@code for (Node n : nodes) {
     *      addNode(n);
     * }}<br>
     * except that this is optimized.
     * @param nodes Nodes to replace.
     */
    public void replaceAll(Collection<Node> nodes) {
        // TODO: add approximate check for whether it would be faster to use AddNodeNoUpdate
        // and then recalculate roots and ends afterwards, or to just use addNode repeatedly.
        // This would mostly involve some computation on
        // the size of nodes and this.nodes, this.rootNodes and this.endNodes

        for (Node n : nodes) {
            replaceNodeNoUpdate(n);
        }

        recalculateRootsAndEnds();
    }

    /**
     * Check whether this graph contains node.
     * @param node Node to check for
     * @return true if this graph contains node, false otherwise.
     */
    public boolean contains(Node node) {
        assert (node != null);

        // search keys since they are sorted / much faster to check
        // (alternative would be `this.nodes.containsValue(node)`)
        return this.nodes.containsKey(node.getIdentifier());
    }

    /**
     * add a new node to nodes.
     * This method throws an Exception when there already exists a node with the same identifier.
     * If you don't want this, use {@link #replaceNode(Node) replaceNode}.
     * @param node Node to add
     * @throws Error If there already was a node with this id, this method throws an Exception.
     */
    public void addNode(Node node) {
        assert (node != null);

        this.addNodeNoUpdate(node);

        this.rootNodes.removeAll(node.getChildren()); // any children of this node are no longer a root
        if (!this.containsAny(node.getParents())) {
            this.rootNodes.add(node); // this node is a root if none of its parents are in this graph
        }

        this.endNodes.removeAll(node.getParents()); // any parents of this node are no longer an end
        if (!this.containsAny(node.getChildren())) {
            this.endNodes.add(node); // this node is an end if none of its children are in this graph
        }
    }

    /**
     * replaces a node with a new Node.
     * This method does not throw an Exception when there already exists a node with the same identifier.
     * If you want that, use {@link #addNode(Node) addNode}.
     * @param node The new Node.
     * @return The old Node, or null if there was none.
     */
    public Node replaceNode(Node node) {
        Node res = this.removeNode(node.getIdentifier());
        this.addNode(node);
        return res;
    }

    /**
     * Remove a Node from this Graph.
     * @param id Id of the node to remove
     * @return the removed Node, or null if there was none.
     */
    public Node removeNode(int id) {
        Node node = this.removeNodeNoUpdate(id);
        if (node == null) {
            return null;
        }

        if (this.rootNodes.remove(node)) {
            // any children of this Node might have become root, if this Node was a root.
            this.checkNodesForRoot(node.getChildren());
        }

        if (this.rootNodes.remove(node)) {
            // any parents of this Node might have become an end Node if this Node was an end Node
            this.checkNodesForEnd(node.getParents());
        }

        return node;
    }

    /**
     * add a new node to nodes. No further processing (like updating the rootNodes or endNodes) is done.
     * This method is to be used when a lot of changes to the graph are going to be made,
     * and it will be more efficient to change them all first and then update everything else.
     * This method throws an Exception when there already exists a node with the same identifier.
     * If you don't want this, use {@link #replaceNodeNoUpdate(Node) replaceNodeNoUpdate}.
     * @param node Node to add
     * @throws Error If there already was a node with this id, this method throws an Exception.
     */
    private void addNodeNoUpdate(Node node) {
        assert (node != null);

        Node res = this.nodes.put(node.getIdentifier(), node);
        if (res != null) {
            // TODO Replace error by a RuntimeExtension (create a specific one)
            // Do this if you are going to use this!
            throw new Error("Tried inserting a node that already exists. Id: " + res.getId());
        }
    }

    /**
     * removes the node with identifier id. No further processing (like updating the rootNodes or endNodes) is done.
     * This method is to be used when a lot of changes to the graph are going to be made,
     * and it will be more efficient to change them all first and then update everything else.
     * @param id The identifier of the Node to be removed
     * @return the removed Node, or null if there was no Node with that id in this Graph.
     */
    private Node removeNodeNoUpdate(int id) {
        return this.nodes.remove(id);
    }

    /**
     * replace a node in nodes. No further processing (like updating the rootNodes or endNodes) is done.
     * This method is to be used when a lot of changes to the graph are going to be made,
     * and it will be more efficient to change them all first and then update everything else.
     * This method does not throw an Exception when there already exists a node with the same identifier.
     * If you want that, use {@link #addNodeNoUpdate(Node) addNodeNoUpdate}.
     * @param node New Node to replace the old one (if any) with.
     * @return The old Node, or null if there was none.
     */
    private Node replaceNodeNoUpdate(Node node) {
        return this.nodes.put(node.getIdentifier(), node);
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
     * Updates the roots and the ends of this Graph. Call this if you have modified the graph structure without
     * this graph knowing about it (e.g. added/removed parents/children to nodes)
     */
    public void recalculateRootsAndEnds() {
        recalculateRoots();
        recalculateEnds();
    }

    /**
     * Recalculate all root nodes.
     */
    public void recalculateRoots() {
        this.rootNodes.clear();
        for (Node n : this.nodes.values()) {
            if (!this.containsAny(n.getParents())) {
                this.rootNodes.add(n);
            }
        }
    }

    /**
     * Recalculate all root nodes.
     */
    public void recalculateEnds() {
        this.endNodes.clear();
        for (Node n : this.nodes.values()) {
            if (!this.containsAny(n.getChildren())) {
                this.endNodes.add(n);
            }
        }
    }

    /**
     * Recheck whether any of the Nodes in nodes are now root.
     * @param nodes Nodes to check.
     */
    private void checkNodesForRoot(Collection<Node> nodes) {
        this.rootNodes.removeAll(nodes);
        for (Node n : nodes) {
            if (!this.containsAny(n.getParents())) {
                this.rootNodes.add(n);
            }
        }
    }

    /**
     * Recheck whether any of the Nodes in nodes are now root.
     * @param nodes Nodes to check.
     */
    private void checkNodesForEnd(Collection<Node> nodes) {
        this.endNodes.removeAll(nodes);
        for (Node n : nodes) {
            if (!this.containsAny(n.getChildren())) {
                this.endNodes.add(n);
            }
        }
    }

    /**
     * Get an iterator over the nodes of this set.
     * Added to make it possible to use a for-each loop. <br>
     * Note that this is not sorted in any ordered.
     * If you want them in topological order, use {@code topoSort().iterator();}
     * @return An iterator
     */
    public Iterator<Node> iterator() {
        return this.nodes.values().iterator();
    }

    /**
     * Topologically sort the nodes from this graph.
     * @return a topologically sorted list of nodes
     */
    public List<Node> topoSort() {
        // TODO: check that graph is not circular. Easiest way is by having a
        // parent-step counter and making sure it doesn't go higher than the number of nodes.

        // topo sorted list
        ArrayList<Node> res = new ArrayList<>(this.nodes.size());

        // nodes that have not yet been added to the list.
        ArrayList<Node> nodes = new ArrayList<>(this.nodes.values());

        // tactic:
        // {
        //   take any node. see if any parents were not added yet.
        //   If so, clearly that parent needs to be added first. Continue searching from parent.
        //   If not, we found a node that can be next in the ordering. Add it to the list.
        // }
        // Repeat until all nodes are added to the list.
        while (!nodes.isEmpty()) {
            Node n = nodes.get(nodes.size());

            findAllParentsAdded:
            while (true) {
                Set<Node> parents = n.getParents();
                for (Node p : parents) {
                    if (nodes.contains(p)) {
                        // there is a parent of n in nodes, so this parent should go before in res.
                        assert (!res.contains(p));

                        // use n = p to continue searching from p.
                        n = p;
                        continue findAllParentsAdded; // continue with the `while(true)`-loop
                    }
                }

                // since we are here, none of the parents of n are in nodes. Thus n can be next in the order.
                assert (nodes.contains(n)); // check that n is in the list of nodes not yet added.
                assert (!res.contains(n)); // check that n was not added yet.

                res.add(n);
                nodes.remove(n);
                break; // break the `while(true)`-loop, continue with the next node n.
            }
        }

        // TODO: use better strategy
        // 1 O(1) create sorted set of (number, node) pairs (sort by number)
        // 2 O(n) for each node, add to set as (node.parents.size(), node)
        // 3 O(n+m) for each node in the list {
        //   3.1 O(1) remove first pair from list (assert that number = 0), add node to resultList.
        //   3.2 O((deg(v)) for each child of node, decrease number by one (make sure that list is still sorted!).
        // }
        // done.

        // TODO: add a test that this works. see assert below.
        // assert that list is sorted?
        // create set found
        // for each node: add to found, check if any of children in found. If yes, fail.
        // if none of the children for all nodes were in found: pass.

        assert (res.size() == this.nodes.size());
        return res;
    }

    /**
     * Calculate and set the locations of all nodes in this graph
     * Important: this assumes that all nodes have the same height!
     * @param xMargin The minimal horizontal distance between two nodes
     * @param yMargin The minimal vertical distance between two nodes
     */
    public void calculateNodeLocations(int xMargin, int yMargin) {
        List<Node> nodes = this.topoSort();
        Set<Node> drawnNodes = new HashSet<>();
        Map<Integer, Set<Node>> lines = new HashMap<>(); // the horizontal "lines" on which nodes are drawn.

        for (Node n : nodes) {
            int maxChildX = 0;
            for (Node c : n.getChildren()) {
                if (drawnNodes.contains(c)) {
                    int childX = c.getRightBorderCenter().getX();
                    if (childX > maxChildX) {
                        maxChildX = childX;
                    }
                }
            }

            // TODO: create a more efficient way of finding y coordinate
            // (use overlapping intervals on x axis to find required height)
            int y = -1;

            lineLoop:
            for (int lineNumber : lines.keySet()) {
                for (Node drawn : lines.get(lineNumber)) {
                    if (drawn.getRightBorderCenter().getX() + xMargin >= maxChildX
                            || drawn.getLeftBorderCenter().getX() < maxChildX + n.getWidth() + xMargin) {
                        continue lineLoop;
                    }
                }
                // we can place it here!
                y = lineNumber;
            }
            if (y < 0) {
                // no line was free, create a new one
                y = lines.size(); // y will be new line number
                lines.put(lines.size(), new HashSet<Node>()); // create new line with next number
            }


            n.setLocation(new XYCoordinate(maxChildX + xMargin, y * (int) n.getHeight()));
        }
    }
}
