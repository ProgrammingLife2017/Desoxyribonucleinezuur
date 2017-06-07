package programminglife.model.drawing;

import programminglife.model.Dummy;
import programminglife.model.Node;
import programminglife.model.XYCoordinate;
import programminglife.utility.Console;

import java.util.*;

/**
 * A part of a {@link programminglife.model.Graph}. It uses a centerNode and a radius.
 * Roughly, every node reachable within radius steps from centerNode is included in this graph.
 * When updating the centerNode or the radius, it also updates the Nodes within this SubGraph.
 */
public class SubGraph {
    /**
     * The amount of padding between layers (horizontal padding).
     */
    private static final int LAYER_PADDING = 20;

    /**
     * The amount of padding between nodes within a Layer (vertical padding).
     */
    private static final int LINE_PADDING = 20;

    private LinkedHashMap<Node, DrawableNode> nodes;
    private DrawableNode centerNode;
    private boolean layout;
    /**
     * The radius around the center node. Eventually,
     * this SubGraph should only include nodes with a *longest* path of at most radius.
     * Radius is zero-based, i.e. a radius of 0 is only the centerNode,
     * radius of 1 is centerNode plus all its children and parents, etc.
     */
    private int radius;

    // TODO: cache topological sorting (inside topoSort(), only recalculate when adding / removing nodes)
    // important: directly invalidate cache (set to null), because otherwise removed nodes
    // can't be garbage collected until next call to topoSort()

    /**
     * Create a SubGraph using a centerNode and a radius around that centerNode.
     * This SubGraph will include all Nodes within radius steps to a parent,
     * and then another 2radius steps to a child, and symmetrically the same with children / parents reversed.
     * @param centerNode The centerNode
     * @param radius The radius
     */
    public SubGraph(DrawableNode centerNode, int radius) {
        // TODO
        // tactic: first go to all parents at exactly radius, then find all children of those parents
        this.centerNode = centerNode;
        this.radius = radius;
        this.layout = false;

        // TODO: also go from all parents to children within 2*radius + 1; and vice-versa from children.
        this.nodes = findParents(centerNode, radius);

        this.nodes.putAll(findChildren(centerNode, radius));
        if (!this.nodes.containsKey(centerNode.getNode())) {
            this.nodes.put(centerNode.getNode(), centerNode);
        }
    }

    // TODO: change findParents and findChildren to reliably only find nodes with a *longest* path of at most radius.
    // (maybe give that their own method, or possibly two methods with a
    // boolean flag for using longest or shortest path as determining factor for the radius)

    /**
     * Find the parents for a single {@link DrawableNode} up to radius.
     * This method returns a set with all nodes with a shortest path of at most radius.
     * @param node The node to start from.
     * @param radius Number indicating the number of steps to take.
     * @return A set of all ancestors within radius steps.
     */
    private LinkedHashMap<Node, DrawableNode> findParents(DrawableNode node, int radius) {
        Set<DrawableNode> nodeSet = new HashSet<>();
        nodeSet.add(node);
        return findParents(nodeSet, radius);
    }

    /**
     * Find the parents for a set of {@link DrawableNode DrawableNodes} up to radius.
     * This method returns a set with all nodes with a shortest
     * path of at most radius to at least one of the nodes in the set.
     * @param nodes The set of nodes to start from.
     * @param radius Number indicating the number of steps to take.
     * @return A set of all ancestors within radius steps.
     */
    private LinkedHashMap<Node, DrawableNode> findParents(Set<DrawableNode> nodes, int radius) {
        LinkedHashMap<Node, DrawableNode> found = new LinkedHashMap<>();
        for (DrawableNode node : nodes) {
            findParents(found, node, radius);
        }
        return found;
    }

    /**
     * Find the parents for a single {@link DrawableNode} up to radius.
     * This method returns a set with all nodes with a shortest path of at most radius.
     * @param found The Set of nodes that have been found. Nodes found by this method will be added to the set.
     * @param node The node to start from.
     * @param radius Number indicating the number of steps to take.
     */
    private void findParents(LinkedHashMap<Node, DrawableNode> found, DrawableNode node, int radius) {
        // TODO: improve datastructure so that parents can be safely skipped if already found
        // it can currently not safely be skipped: 0-1-2-3-4
        //                                            \_/
        // assuming radius 3, if you find the nodes in the order 0, 1, 2, 3,
        // you cannot skip 3 as that would then miss 4 (which is also within radius 3, via 0-1-3-4)
        //Also check the childeren if one of the nodes has not been added yet.
        for (Node child : node.getChildren()) {
            if (!found.containsKey(child)) {
                found.put(child, new DrawableNode(child));
                findChildren(found, found.get(child), 2 * this.radius - radius - 1);
            }
        }
        if (radius <= 0) {
            return;
        }
        radius--; // decrease radius once instead of multiple times within the loop;
        for (Node parent : node.getParents()) {
            if (!found.containsKey(parent)) {
                found.put(parent, new DrawableNode(parent));
                findParents(found, found.get(parent), radius);
            }

        }

    }

    /**
     * Find the parents for a single {@link DrawableNode} up to radius.
     * This method returns a set with all nodes with a shortest path of at most radius.
     * @param node The node to start from.
     * @param radius Number indicating the number of steps to take.
     * @return A set of all ancestors within radius steps.
     */
    private LinkedHashMap<Node, DrawableNode> findChildren(DrawableNode node, int radius) {
        Set<DrawableNode> nodeSet = new HashSet<>();
        nodeSet.add(node);
        return findChildren(nodeSet, radius);
    }

    /**
     * Find the parents for a set of {@link DrawableNode DrawableNodes} up to radius.
     * This method returns a set with all nodes with a shortest
     * path of at most radius to at least one of the nodes in the set.
     * @param nodes The set of nodes to start from.
     * @param radius Number indicating the number of steps to take.
     * @return A set of all ancestors within radius steps.
     */
    private LinkedHashMap<Node, DrawableNode> findChildren(Set<DrawableNode> nodes, int radius) {
        LinkedHashMap<Node, DrawableNode> found = new LinkedHashMap<>();

        //Put all node that are already found by findparents into the find.
        for (DrawableNode node: nodes) {
           found.put(node.getNode(), node);
        }
        for (DrawableNode node : nodes) {
            findChildren(found, node, radius);
        }
        return found;
    }

    /**
     * Find the parents for a single {@link DrawableNode} up to radius.
     * This method returns a set with all nodes with a shortest path of at most radius.
     * @param found The Set of nodes that have been found. Nodes found by this method will be added to the set.
     * @param node The node to start from.
     * @param radius Number indicating the number of steps to take.
     */
    private void findChildren(LinkedHashMap<Node, DrawableNode> found, DrawableNode node, int radius) {
        // TODO: improve datastructure so that parents can be safely skipped if already found
        // it can currently not safely be skipped: 0-1-2-3-4
        //                                            \_/
        // assuming radius 3, if you find the nodes in the order 0, 1, 2, 3,
        // you cannot skip 3 as that would then miss 4 (which is also within radius 3, via 0-1-3-4)
        for (Node parent : node.getParents()) {
            if (!found.containsKey(parent)) {
                found.put(parent, new DrawableNode(parent));
                findParents(found, found.get(parent), 2 * this.radius - radius - 1);
            }

        }
        if (radius <= 0) {
            return;
        }
        radius--; // decrease radius once instead of multiple times within the loop;
        for (Node child : node.getChildren()) {
            if (!found.containsKey(child)) {
                found.put(child, new DrawableNode(child));
                findChildren(found, found.get(child), radius);
            }
        }


    }

    /**
     * Draw this SubGraph on the screen.
     */
    public void draw() {

    }

    /**
     * Find out which {@link Drawable} is at the given location.
     * @param loc The location to search for Drawables.
     * @return The {@link Drawable} that is on top at the given location.
     */
    public Drawable atLocation(XYCoordinate loc) {
        return this.atLocation(loc.getX(), loc.getY());
    }

    /**
     * Find out which {@link Drawable} is at the given location.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Drawable} that is on top at the given location.
     */
    public Drawable atLocation(double x, double y) {
        // TODO: implement;
        throw new Error("Not implemented yet");
    }

    /**
     * Lay out the {@link Drawable Drawables} in this SubGraph.
     */
    public void layout() {
        if (layout) {
            return;
        }
        List<Layer> layers = findLayers();
        createDummyNodes(layers);
        sortWithinLayers(layers);


        int x = 50;
        for (Layer layer : layers) {
            int y = 50;
            x += LAYER_PADDING * 0.3 * layer.size();
            for (DrawableNode d : layer) {
                d.setLocation(new XYCoordinate(x, y));
                y += LINE_PADDING;
            }
            x += layer.getWidth() + LAYER_PADDING * 0.3 * layer.size();
        }
        layout = true;
        // TODO: translate so that the centerNode is at 0,0;
    }

    /**
     * Create Dummy nodes for layers to avoid more crossing edges.
     * @param layers {@link List} representing all layers to be drawn.
     */
    private void createDummyNodes(List<Layer> layers) {
        Layer current = new Layer();
        for (Layer next : layers) {
            for (DrawableNode node : current) {
                for (DrawableNode child : this.getChildren(node)) {
                    if (!next.contains(child)) {
                        DrawableNode dummy = new DrawableNode(
                                new Dummy(node.getNode(), child.getNode(), node.getLink(child))
                        );
                        node.replaceChild(child, dummy);
                        child.replaceParent(node, dummy);
                        dummy.setWidth(next.getWidth());
                        this.nodes.put(dummy.getNode(), dummy);
                        next.add(dummy);
                    }
                }
            }
            current = next;
        }
    }

    /**
     * Put all nodes in {@link Layer Layers}. This method is used when {@link #layout() laying out} the graph.
     * This will put each node in a Layer one higher than each of its parents.
     * @return A {@link List} of Layers with all the nodes (all nodes are divided over the Layers).
     */
    private List<Layer> findLayers() {
        long startTime = System.nanoTime();
        List<DrawableNode> sorted = topoSort();
        long finishTime = System.nanoTime();
        long differenceTime = finishTime - startTime;
        long milisecondTime = differenceTime / 1000000;
        Console.println("TIMERINO OF TOPOSORTERINO:  " + milisecondTime);
        Console.println("Amount of nodes: " + sorted.size());
        Map<DrawableNode, Integer> nodeLevel = new HashMap<>();
        List<Layer> layerList = new ArrayList<>();

        for (DrawableNode node : sorted) {
            int maxParentLevel = -1;
            for (DrawableNode parent : this.getParents(node)) {
                Integer parentLevel = nodeLevel.get(parent);
                if (parentLevel == null) {
                    continue;
                } else if (maxParentLevel < parentLevel) {
                    maxParentLevel = parentLevel;
                }
            }
            maxParentLevel++; // we want this node one level higher than the highest parent.
            nodeLevel.put(node, maxParentLevel);
            if (layerList.size() <= maxParentLevel) {
                layerList.add(new Layer());
            }
            layerList.get(maxParentLevel).add(node);
        }

        return layerList;
    }

    /**
     * Get the parents of {@link DrawableNode} node.
     * @param node The {@link DrawableNode} to get
     * @return A {@link Collection} of {@link DrawableNode}
     */
    public Collection<DrawableNode> getParents(DrawableNode node) {
        Collection<DrawableNode> parents = new LinkedHashSet<>();
        for (Node parentNode : node.getParents()) {
            if (this.nodes.containsKey(parentNode)) {
                parents.add(this.nodes.get(parentNode));
            }
        }
        return parents;
    }

    /**
     * Get the children of {@link DrawableNode} node.
     * @param node The {@link DrawableNode} to get
     * @return A {@link Collection} of {@link DrawableNode}
     */
    public Collection<DrawableNode> getChildren(DrawableNode node) {
        Collection<DrawableNode> children = new LinkedHashSet<>();
        for (Node childNode : node.getChildren()) {
            if (this.nodes.containsKey(childNode)) {
                children.add(this.nodes.get(childNode));
            }
        }
        return children;
    }

    /**
     * Sort each Layer to minimize edge crossings between the Layers.
     * @param layers The layers to sort.
     */
    private void sortWithinLayers(List<Layer> layers) {
        // TODO: improve to reduce edge crossings
        // note: in case of ambiguity in choosing what node to draw first, use node with lowest id
        // (to break ties and make layout deterministic)

        // For each edge: place it on the row it came from or lower.
        // Start from sorting in previous layer.
        // note: this is here as a skeleton for sorting. It compiles, but doesn't
        // do anything useful, so it is commented out.
//        for (Layer l : layers) {
//            l.sort(new Comparator<DrawableNode>() {
//                @Override
//                public int compare(DrawableNode o1, DrawableNode o2) {
//                    return 0;
//                }
//            });
//        }
    }

    /**
     * Topologically sort the nodes from this graph.
     *
     * Assumption: graph is a DAG.
     * @return a topologically sorted list of nodes
     */
    public List<DrawableNode> topoSort() {
        // topo sorted list
        ArrayList<DrawableNode> res = new ArrayList<>(this.nodes.size());

        // nodes that have not yet been added to the list.
        LinkedHashSet<DrawableNode> found = new LinkedHashSet<>();

        // tactic:
        // {
        //   take any node. see if any parents were not added yet.
        //   If so, clearly that parent needs to be added first. Continue searching from parent.
        //   If not, we found a node that can be next in the ordering. Add it to the list.
        // }
        // Repeat until all nodes are added to the list.
        for (DrawableNode n : this.nodes.values()) {
            if (!found.add(n)) {
                continue;
            }
            topoSortFromNode(res, found, n);
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
     * Toposort all ancestors of a node.
     * @param result The reult list to which these nodes will be added,
     * @param found The nodes that have already been found,
     * @param node The node to start seaching from.
     */
    private void topoSortFromNode(ArrayList<DrawableNode> result,
                                  LinkedHashSet<DrawableNode> found, DrawableNode node) {
        for (Node parent : node.getParents()) {
            DrawableNode drawableParent = this.nodes.get(parent);
            if (drawableParent != null && found.add(drawableParent)) {
                topoSortFromNode(result, found, drawableParent);
            }
        }
        result.add(node);
    }

    /**
     * Set the centerNode of this SubGraph.
     * Nodes that are now outside the radius of this SubGraph will be removed,
     * and Nodes that are now inside will be added.
     * @param centerNode The new centerNode.
     */
    public void setCenterNode(Node centerNode) {
        // TODO
        // drop nodes that are now outside radius
        // include nodes that have come into radius
        // drop nodes that are only connected via nodes now outside radius?
    }

    /**
     * Set the radius of this SubGraph.
     * Nodes that are now outside the radius of this SubGraph will be removed,
     * and Nodes that are now inside will be added.
     * @param radius The new radius.
     */
    public void setRadius(int radius) {
        // TODO
        // when getting bigger: include new nodes
        // when getting smaller: drop nodes outside new radius.
    }

    public LinkedHashMap<Node, DrawableNode> getNodes() {
        return this.nodes;
    }
}
