package programminglife.model.drawing;

import org.jetbrains.annotations.NotNull;
import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.*;

// TODO: rename to better name
public class Drawing<N extends DrawableNode<N>> {
    /**
     * How much do we go further than radius to make sure we don't miss any important nodes?
     */
    private static final int OVERSHOOT = 2;
    private static final int LAYER_PADDING = 20;
    private static final int LINE_PADDING = 20;

    private Set<DrawableNode<N>> nodes;
    private Set<DrawableEdge<N>> edges;
    private Node centerNode;
    private int radius;

    public Drawing(Node centerNode, int radius) {
        // TODO
        // tactic: first go to all parents at exactly radius, then find all children of those parents
    }

    public void draw() {
        // TODO
        // use layout if not done already
    }

    public Drawable atLocation(XYCoordinate loc) {
        // TODO
        return null;
    }

    public void layout() {
        // TODO
        List<Layer<N>> layers = findLayers();

        int x = 0;
        int y = 0;
        for (Layer<N> layer : layers) {
            for (DrawableNode<N> d : layer) {
                d.setLocation(new XYCoordinate(x, y));
                y += LINE_PADDING;
            }
            x += layer.getWidth() + LAYER_PADDING;
        }

        // TODO: translate so that the centernode is at 0,0;
    }

    private List<Layer<N>> findLayers() {
        List<DrawableNode<N>> sorted = topoSort();
        Map<DrawableNode<N>, Integer> nodeLevel = new HashMap<>();
        List<Layer<N>> res = new ArrayList<>();

        for (DrawableNode<N> node : sorted) {
            int maxParentLevel = -1;
            for (DrawableEdge<N> edge : node.getParents()) {
                Integer parentLevel = nodeLevel.get(edge.getStart());
                if (parentLevel == null) {
                    continue;
                } else if (maxParentLevel < parentLevel) {
                    maxParentLevel = parentLevel;
                }
            }
            maxParentLevel++; // we want this node one level higher than the highest parent.
            nodeLevel.put(node, maxParentLevel);
            if (res.size() <= maxParentLevel) {
                res.add(new Layer<>());
            }
            res.get(maxParentLevel).add(node);
        }

        // TODO: create dummy nodes for edges
        return res;
    }

    private void sortWithinLayers(List<Layer<N>> layers) {
        // TODO: improve to reduce edge crossings
        // note: in case of ambiguity in choosing what node to draw first, use node with lowest id
        // (to break ties and make layout deterministic)
        for (Layer<N> l : layers) {
            l.sort(Comparator.comparingInt(Node::getIdentifier));
        }
    }

    /**
     * Topologically sort the nodes from this graph.
     * @return a topologically sorted list of nodes
     */
    public List<DrawableNode<N>> topoSort() {
        // TODO: check that graph is not circular. Easiest way is by having a
        // parent-step counter and making sure it doesn't go higher than the number of nodes.

        // topo sorted list
        ArrayList<DrawableNode<N>> res = new ArrayList<>(this.nodes.size());

        // nodes that have not yet been added to the list.
        ArrayList<DrawableNode<N>> nodes = new ArrayList<>(this.nodes);

        // tactic:
        // {
        //   take any node. see if any parents were not added yet.
        //   If so, clearly that parent needs to be added first. Continue searching from parent.
        //   If not, we found a node that can be next in the ordering. Add it to the list.
        // }
        // Repeat until all nodes are added to the list.
        while (!nodes.isEmpty()) {
            DrawableNode<N> n = nodes.get(nodes.size() - 1);

            findAllParentsAdded:
            while (true) {
                Collection<DrawableEdge<N>> parents = n.getParents();
                for (DrawableEdge<N> e : parents) {
                    DrawableNode<N> p = e.getStart();
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

    public void setCenterNode(Node centerNode) {
        // TODO
        // drop nodes that are now outside radius
        // include nodes that have come into radius
        // drop nodes that are only connected via nodes now outside radius?
    }

    public void setRadius(int radius) {
        // TODO
        // when getting bigger: include new nodes
        // when getting smaller: drop nodes outside new radius.
    }
}
