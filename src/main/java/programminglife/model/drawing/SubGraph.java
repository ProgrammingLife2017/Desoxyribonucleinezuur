package programminglife.model.drawing;

import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;
import programminglife.utility.Console;

import java.util.*;

/**
 * A part of a {@link programminglife.model.GenomeGraph}. It uses a centerNode and a radius.
 * Roughly, every node reachable within radius steps from centerNode is included in this graph.
 * When updating the centerNode or the radius, it also updates the Nodes within this SubGraph.
 */
public class SubGraph {
    private static final int DEFAULT_DYNAMIC_RADIUS = 50;
    private static final int DEFAULT_NODE_Y = 50;
    private static final int BORDER_BUFFER = 40;

    private static final int MIN_RADIUS_DEFAULT = 50;
    /**
     * The amount of padding between layers (horizontal padding).
     */
    private double layerPadding = 20;

    private double diffLayerPadding = 3;

    /**
     * The amount of padding between nodes within a Layer (vertical padding).
     */
    private GenomeGraph graph;
    private LinkedHashMap<Integer, DrawableNode> nodes;
    private LinkedHashMap<Integer, DrawableNode> rootNodes;
    private LinkedHashMap<Integer, DrawableNode> endNodes;

    private ArrayList<Layer> layers;

    // TODO: cache topological sorting (inside topoSort(), only recalculate when adding / removing nodes)
    // important: directly invalidate cache (set to null), because otherwise removed nodes
    // can't be garbage collected until next call to topoSort()

    /**
     * Create a SubGraph from a graph, without any nodes initially.
     * @param graph The {@link GenomeGraph} that this SubGraph is based on.
     */
    private SubGraph(GenomeGraph graph) {
        this(graph, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    /**
     * Create a SubGraph with the specified nodes, rootNodes and endNodes.
     * @param graph The {@link GenomeGraph} that this SubGraph is based on.
     * @param nodes The nodes of this SubGraph.
     * @param rootNodes The rootNodes of this SubGraph.
     * @param endNodes The endNodes of this SubGraph.
     */
    private SubGraph(GenomeGraph graph, LinkedHashMap<Integer, DrawableNode> nodes,
                     LinkedHashMap<Integer, DrawableNode> rootNodes, LinkedHashMap<Integer, DrawableNode> endNodes) {
        this.graph = graph;
        this.nodes = nodes;
        this.rootNodes = rootNodes;
        this.endNodes = endNodes;

        this.createLayers();
    }

    /**
     * Create a SubGraph using a centerNode and a radius around that centerNode.
     * This SubGraph will include all Nodes within radius steps to a parent,
     * and then another 2radius steps to a child, and symmetrically the same with children / parents reversed.
     *
     * @param centerNode The centerNode
     * @param radius     The radius
     */
    public SubGraph(DrawableSegment centerNode, int radius) {
        this(centerNode, MIN_RADIUS_DEFAULT, Math.max(radius, MIN_RADIUS_DEFAULT));

        Layer firstLayer = layers.get(0);
        assert (firstLayer != null);

        firstLayer.setX(0);
        firstLayer.setDrawLocations(DEFAULT_NODE_Y);
        this.setRightDrawLocations(this.layers, 0);

        double center = this.layers.get(this.layers.size() - 1).getX() / 2;
        this.translate(-center);
    }

    /**
     * Create a SubGraph using a centerNode and a radius around that centerNode.
     * This SubGraph will include all Nodes within radius steps to a parent,
     * and then another 2radius steps to a child, and symmetrically the same with children / parents reversed.
     *
     * @param centerNode The centerNode
     * @param minRadius  The minimum radius.
     * @param radius     The radius
     */
    SubGraph(DrawableSegment centerNode, int minRadius, int radius) {
        assert (minRadius <= radius);

        this.graph = centerNode.getGraph();
        this.layers = null;

        findNodes(this, Collections.singleton(centerNode), new LinkedHashMap<>(), radius);

        layout();
    }

    // TODO: change findParents and findChildren to reliably only find nodes with a *longest* path of at most radius.
    // (maybe give that their own method, or possibly two methods with a
    // boolean flag for using longest or shortest path as determining factor for the radius)

    /**
     * Find nodes within radius steps from centerNode.
     * This resets the {@link #nodes}, {@link #rootNodes} and {@link #endNodes}
     *
     * @param subGraph The SubGraph to find these nodes for.
     * @param startNodes The Nodes to start searching from.
     * @param excludedNodes The nodes that will not be added to this graph, even if they are found.
     * @param radius The number of steps to search.
     */
    private static void findNodes(SubGraph subGraph, Collection<DrawableNode> startNodes,
                                  LinkedHashMap<Integer, DrawableNode> excludedNodes, int radius) {
        long startTime = System.nanoTime();

        subGraph.nodes = new LinkedHashMap<>();
        subGraph.rootNodes = new LinkedHashMap<>();
        subGraph.endNodes = new LinkedHashMap<>();

        /*
         * The queue for the BFS. This Queue uses null as separators between radii.
         * Example: A B null C D: first A, then B, then null, so we go to the next layer, then C, then D
         * layer 1:  A B
         * layer 2:  C D
         */
        Queue<FoundNode> queue = new LinkedList<>();
        startNodes.forEach(node -> queue.add(new FoundNode(node, null)));
        queue.add(null);

        boolean lastRow = radius == 0;
        while (!queue.isEmpty()) {
            FoundNode current = queue.poll(); // Note: may still be null if the actual element is null!

            if (current == null) {
                radius--;

                if (radius == 0) {
                    lastRow = true;
                } else if (radius < 0) {
                    break;
                }

                queue.add(null);
                continue;
            }

            if (!startNodes.contains(current.node) && excludedNodes.containsKey(current.node.getIdentifier())) {
                continue;
            }

            // save this node, save the result to check whether we had found it earlier.
            DrawableNode previous = subGraph.nodes.put(current.node.getIdentifier(), current.node);

            if (lastRow) {
                // last row, add this node to rootNodes / endNodes even if we already found this node
                // (for when a node is both a root and an end node)
                if (current.foundFrom == FoundNode.FoundFrom.CHILD) {
                    subGraph.rootNodes.put(current.node.getIdentifier(), current.node);
                } else if (current.foundFrom == FoundNode.FoundFrom.PARENT) {
                    subGraph.endNodes.put(current.node.getIdentifier(), current.node);
                }
                // else: current.foundFrom == null, true for the centerNode.
                // Note: since radius is always at least MIN_RADIUS, we could else instead of else if.
                // But that would be premature optimization.
            } else if (previous != null) {
                // we already found this node, continue to next node.
                assert (previous.equals(current.node));
            } else {
                // this is not the last row, and this node was not added yet
                Collection<Integer> children = current.node.getChildren();
                Collection<Integer> parents = current.node.getParents();

                children.forEach(node -> queue.add(
                        new FoundNode(new DrawableSegment(subGraph.graph, node), FoundNode.FoundFrom.PARENT)));
                parents.forEach(node -> queue.add(
                        new FoundNode(new DrawableSegment(subGraph.graph, node), FoundNode.FoundFrom.CHILD)));
            }
        }

        long endTime = System.nanoTime();
        long difference = endTime - startTime;
        double difInSeconds = difference / 1000000000.0;
        Console.println("Time for finding nodes: %f s", difInSeconds);
    }

    public void checkDynamicLoad(int leftBorder, double rightBorder) {
        if (layers.get(BORDER_BUFFER).getX() > leftBorder) {
            this.addFromRootNodes(SubGraph.DEFAULT_DYNAMIC_RADIUS);
        }
        if (layers.get(layers.size() - BORDER_BUFFER - 1).getX() < rightBorder) {
            this.addFromEndNodes(SubGraph.DEFAULT_DYNAMIC_RADIUS);
        }
    }

    /**
     * A class for keeping track of how a Node was found. Only used within {@link SubGraph#findNodes}.
     */
    private static final class FoundNode {
        /**
         * Whether a node was found from a parent or a child.
         */
        private enum FoundFrom { PARENT, CHILD }
        private DrawableNode node;
        private FoundFrom foundFrom;

        /**
         * simple constructor for a FoundNode.
         * @param node The node that was found.
         * @param foundFrom Whether it was found from a parent or a child.
         */
        private FoundNode(DrawableNode node, FoundFrom foundFrom) {
            this.node = node;
            this.foundFrom = foundFrom;
        }
    }

    /**
     * Find out which {@link Drawable} is at the given location.
     *
     * @param loc The location to search for Drawables.
     * @return The {@link Drawable} that is on top at the given location.
     */
    public Drawable atLocation(XYCoordinate loc) {
        return this.atLocation(loc.getX(), loc.getY());
    }

    /**
     * Find out which {@link Drawable} is at the given location.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Drawable} that is on top at the given location.
     */
    public Drawable atLocation(double x, double y) {
        int layerIndex = getLayerIndex(this.layers, x);

        // TODO: implement;
        // 1: check that x is actually within the layer.
        // 2: check nodes in layer for y coordinate.
        throw new Error("Not implemented yet");
    }

    /**
     * Get the index of the {@link Layer} closest to an x coordinate.
     * If two layers are equally close (x is exactly in the middle of end
     * of left layer and start of the right layer), the right Layer is returned, as this
     * one is likely to have nodes closer (nodes in the left layer do not necessarily extend to the end)
     * @param x The coordinate
     * @param layers The list of layers to look through.
     * @return The index of the closest layer.
     */
    private int getLayerIndex(List<Layer> layers, double x) {
        int resultIndex = Collections.binarySearch(layers, x);
        if (resultIndex >= layers.size()) {
            // x is right outside list, last layer is closest.
            return layers.size() - 1;
        } else if (resultIndex >= 0) {
            // x is exactly at the start of a layer, that layer is closest.
            return resultIndex;
        } else {
            // x < 0, -x is the closest layer on the right, -x - 1 is the closest
            // layer on the left. (see binarySearch documentation)
            // check which of the two layers is closest.
            int rightLayerIndex = -resultIndex;
            int leftLayerIndex = -resultIndex - 1;
            Layer rightLayer = layers.get(rightLayerIndex);
            Layer leftLayer = layers.get(leftLayerIndex);

            if (rightLayer.getX() - x > x - (leftLayer.getX() + leftLayer.getWidth())) {
                // distance from right layer is greater, so left layer is closer
                return leftLayerIndex;
            } else {
                return rightLayerIndex;
            }
        }
    }

    /**
     * Lay out the {@link Drawable Drawables} in this SubGraph.
     */
    public void layout() {
        createLayers();

        int minimumLayerIndex = findMinimumNodesLayerIndex(this.layers);
        sortLayersLeftFrom(minimumLayerIndex);
        sortLayersRightFrom(minimumLayerIndex);
    }

    /**
     * Assign nodes to {@link Layer layers} and create dummyNodes for edges that span multiple layers.
     */
    private void createLayers() {
        this.layers = findLayers();

        createDummyNodes(layers);
    }

    /**
     * Set the coordinates for all {@link Layer layers} to the right of the given Layer.
     * @param layers The Layers to set the coordinates for.
     * @param setLayerIndex The index of the Layer to start from (exclusive,
     *                      so coordinates are not set for this layer).
     */
    public void setRightDrawLocations(ArrayList<Layer> layers, int setLayerIndex) {
        ListIterator<Layer> layerIterator = layers.listIterator(setLayerIndex);
        Layer setLayer = layerIterator.next();
        double x = setLayer.getX() + setLayer.getWidth();
        int size = setLayer.size();

        while (layerIterator.hasNext()) {
            Layer layer = layerIterator.next();

            int newSize = layer.size();
            int diff = Math.abs(newSize - size);
            x += layerPadding + diffLayerPadding * diff;

            layer.setX(x);
            layer.setDrawLocations(DEFAULT_NODE_Y);
            x += layer.getWidth() + layerPadding * 0.1 + newSize;
            size = newSize;
        }
    }

    /**
     * Set the coordinates for all {@link Layer layers} to the left of the given Layer.
     * @param layers The Layers to set the coordinates for.
     * @param setLayerIndex The index of the Layer to start from (exclusive,
     *                      so coordinates are not set for this layer).
     */
    public void setLeftDrawLocations(ArrayList<Layer> layers, int setLayerIndex) {
        ListIterator<Layer> layerIterator = layers.listIterator(setLayerIndex + 1);
        Layer setLayer = layerIterator.previous();
        double x = setLayer.getX();
        int size = setLayer.size();

        while (layerIterator.hasPrevious()) {
            Layer layer = layerIterator.previous();

            int newSize = layer.size();
            int diff = Math.abs(newSize - size);
            x -= (layer.getWidth() + layerPadding * 1.1 * newSize + diffLayerPadding * diff);

            layer.setX(x);
            layer.setDrawLocations(DEFAULT_NODE_Y);
            size = newSize;
        }
    }

    /**
     * Create {@link DrawableDummy} nodes for layers to avoid more crossing edges.
     *
     * @param layers {@link List} representing all layers to be drawn.
     */
    private void createDummyNodes(List<Layer> layers) {
        Layer current = new Layer();
        for (Layer next : layers) {
            for (DrawableNode node : current) {
                for (DrawableNode child : this.getChildren(node)) {
                    if (!next.contains(child)) {
                        DrawableDummy dummy = new DrawableDummy(
                                DrawableNode.getUniqueId(), node, child, this.getGraph());
                        node.replaceChild(child, dummy);
                        child.replaceParent(node, dummy);
                        dummy.setWidth(next.getWidth());
                        this.nodes.put(dummy.getIdentifier(), dummy);
                        next.add(dummy);
                    }
                }
            }
            current = next;
        }
    }

    /**
     * Put all nodes in {@link Layer Layers}. This method is used when {@link #layout laying out} the graph.
     * This will put each node in a Layer one higher than each of its parents.
     *
     * @return A {@link List} of Layers with all the nodes (all nodes are divided over the Layers).
     */
    private ArrayList<Layer> findLayers() {
        long startTime = System.nanoTime();
        List<DrawableNode> sorted = topoSort();
        long finishTime = System.nanoTime();
        long differenceTime = finishTime - startTime;
        long millisecondTime = differenceTime / 1000000;
        Console.println("TIME OF TOPOSORT:  " + millisecondTime);
        Console.println("Amount of nodes: " + sorted.size());
        Map<DrawableNode, Integer> nodeLevel = new HashMap<>();
        ArrayList<Layer> layerList = new ArrayList<>();

        for (DrawableNode node : sorted) {
            int maxParentLevel = -1;
            for (DrawableNode parent : this.getParents(node)) {
                Integer parentLevel = nodeLevel.get(parent);
                if (maxParentLevel < parentLevel) {
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
     *
     * @param node The {@link DrawableNode} to get
     * @return A {@link Collection} of {@link DrawableNode}
     */
    public Collection<DrawableNode> getParents(DrawableNode node) {
        Collection<DrawableNode> parents = new LinkedHashSet<>();
        for (int parentID : node.getParents()) {
            if (this.nodes.containsKey(parentID)) {
                parents.add(this.nodes.get(parentID));
            }
        }
        return parents;
    }

    /**
     * Get the children of {@link DrawableNode} node.
     *
     * @param node The {@link DrawableNode} to get
     * @return A {@link Collection} of {@link DrawableNode}
     */
    public Collection<DrawableNode> getChildren(DrawableNode node) {
        Collection<DrawableNode> children = new LinkedHashSet<>();
        for (int childID : node.getChildren()) {
            if (this.nodes.containsKey(childID)) {
                children.add(this.nodes.get(childID));
            }
        }
        return children;
    }

    /**
     * Find the Layer with the least number of nodes.
     * @param layers The {@link Layer Layers} to search through.
     * @return The index of the Layer with the minimum number of nodes, or -1 if the list of layers is empty.
     */
    private int findMinimumNodesLayerIndex(List<Layer> layers) {
        // find a layer with a single node

        int index = -1;
        int min = Integer.MAX_VALUE;

        Iterator<Layer> layerIterator = layers.iterator();
        for (int i = 0; layerIterator.hasNext(); i++) {
            Layer currentLayer = layerIterator.next();

            int currentSize = currentLayer.size();
            if (currentSize < min) {
                min = currentSize;
                index = i;
                if (currentSize <= 1) {
                    // There should be no layers with less than 1 node,
                    // so this layer has the least amount of nodes.
                    break;
                }
            }
        }

        return index;
    }

    /**
     * Sort all {@link Layer Layers} right from a given layer.
     * @param layerIndex The index of the layer to start sorting from (exclusive, so that layer is not sorted).
     */
    private void sortLayersRightFrom(int layerIndex) {
        ListIterator<Layer> iterator = layers.listIterator(layerIndex);
        Layer prev = iterator.next();

        while (iterator.hasNext()) {
            Layer layer = iterator.next();
            layer.sort(this, prev, true);
            prev = layer;
        }
    }

    /**
     * Sort all {@link Layer Layers} left from a given layer.
     * @param layerIndex The index of the layer to start sorting from (exclusive, so that layer is not sorted).
     */
    private void sortLayersLeftFrom(int layerIndex) {
        ListIterator<Layer> iterator = layers.listIterator(layerIndex + 1);
        Layer prev = iterator.previous();

        while (iterator.hasPrevious()) {
            Layer layer = iterator.previous();
            layer.sort(this, prev, false);
            prev = layer;
        }
    }

    /**
     * Topologically sort the nodes from this graph. <br />
     *
     * Assumption: graph is a DAG.
     *
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
     *
     * @param result The result list to which these nodes will be added,
     * @param found  The nodes that have already been found,
     * @param node   The node to start searching from.
     */
    private void topoSortFromNode(ArrayList<DrawableNode> result,
                                  LinkedHashSet<DrawableNode> found, DrawableNode node) {
        for (int parentID : node.getParents()) {
            DrawableNode drawableParent = this.nodes.get(parentID);
            if (drawableParent != null && found.add(drawableParent)) {
                topoSortFromNode(result, found, drawableParent);
            }
        }
        result.add(node);
    }

    /**
     * Add nodes from the {@link #rootNodes}.
     * @param radius The number of steps to take from the rootNodes before stopping the search.
     */
    private void addFromRootNodes(int radius) {
        Console.println("Increasing graph with radius %d", radius);
        SubGraph subGraph = new SubGraph(graph);

        findNodes(subGraph, layers.get(0).getNodes(), this.nodes, radius);
        subGraph.createLayers();

        this.mergeLeftSubGraphIntoThisSubGraph(subGraph);
    }

    /**
     * Add nodes from the {@link #endNodes}.
     * @param radius The number of steps to take from the endNodes before stopping the search.
     */
    public void addFromEndNodes(int radius) {
        // TODO: copy from addFromRootNodes
        throw new Error("Not implemented yet");
    }

    /**
     * Merge another {@link SubGraph} into this SubGraph, by putting it on the left of this SubGraph,
     * and then drawing connecting edges between nodes that should have them.
     * @param leftSubGraph The other SubGraph that will be merged into this one.
     */
    private void mergeLeftSubGraphIntoThisSubGraph(SubGraph leftSubGraph) {
        this.nodes.putAll(leftSubGraph.nodes);
        this.rootNodes = leftSubGraph.rootNodes;
        leftSubGraph.endNodes.forEach((id, node) -> {
            boolean addToRootNodes = false;
            for (Integer childId : node.getChildren()) {
                if (this.nodes.containsKey(childId)) {
                    addToRootNodes = true;
                    break;
                }
            }
            if (!addToRootNodes) {
                for (Integer parentId : node.getParents()) {
                    if (this.nodes.containsKey(parentId)) {
                        addToRootNodes = true;
                        break;
                    }
                }
            }
            if (addToRootNodes) {
                this.rootNodes.put(id, node);
            }
        });

        int oldFirstIndex = leftSubGraph.layers.size();
        this.layers.addAll(0, leftSubGraph.layers);


        // TODO: find DummyNodes between subgraphs. Just use findDummyNodes on full graph?
//        throw new Error("Not implemented yet");

        this.sortLayersLeftFrom(oldFirstIndex);
        this.setLeftDrawLocations(this.layers, oldFirstIndex);
    }

    /**
     * Set the radius of this SubGraph.
     * Nodes that are now outside the radius of this SubGraph will be removed,
     * and Nodes that are now inside will be added.
     *
     * @param radius The new radius.
     */
    public void setRadius(int radius) {
        // TODO
        // when getting bigger: include new nodes
        // when getting smaller: drop nodes outside new radius.
    }

    public LinkedHashMap<Integer, DrawableNode> getNodes() {
        return this.nodes;
    }

    public GenomeGraph getGraph() {
        return graph;
    }

    public void scaleLayerPadding(double zoom) {
        this.layerPadding /= zoom;
        this.diffLayerPadding /= zoom;
    }

    public void translate(double diff) {
        for (Layer layer : this.layers) {
            layer.setX(layer.getX() + diff);
            for (DrawableNode node : layer) {
                node.setLocation(node.getLocation().getX() + diff, node.getLocation().getY());
            }
        }
    }


}
