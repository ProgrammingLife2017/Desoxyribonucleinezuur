package programminglife.model.drawing;

import org.jetbrains.annotations.NotNull;
import programminglife.model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A layer of {@link DrawableNode DrawableNodes}. Multiple Layers are used to lay out the graph.
 *
 * @param <N> The type of DrawableNodes this Layer stores.
 * @see SubGraph#layout()
 * @see SubGraph#findLayers()
 */
public class Layer<N extends Node<N>> implements Iterable<N> {
    private int width;
    private List<N> nodes;

    /**
     * Default empty constructor.
     */
    public Layer() {
        this.width = 0;
        this.nodes = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Add a {@link DrawableNode} to this Layer.
     * @param node the node to add.
     */
    public void add(N node) {
        this.nodes.add(node);
    }

    /**
     * Get an iterator over the {@link DrawableNode DrawableNodes} in this Layer.
     * @return an iterator over the {@link DrawableNode DrawableNodes} in this Layer.
     */
    @NotNull
    @Override
    public Iterator<N> iterator() {
        return nodes.iterator();
    }

    /**
     * sort the {@link DrawableNode DrawableNodes} in this layer according to the order of the {@link Comparator} c.
     * @param c A {@link Comparator} which imposes a total ordering on the nodes.
     */
    public void sort(Comparator<? super N> c) {
        this.nodes.sort(c);
    }
}
