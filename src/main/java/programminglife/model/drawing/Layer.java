package programminglife.model.drawing;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ivo on 2017-05-23.
 */
public class Layer<N extends DrawableNode<N>> implements Iterable<DrawableNode<N>> {
    private int width;
    private List<DrawableNode<N>> nodes;

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

    public void add(DrawableNode<N> node) {
        this.nodes.add(node);
    }

    @NotNull
    @Override
    public Iterator<DrawableNode<N>> iterator() {
        return nodes.iterator();
    }

    public void sort(Comparator<? super DrawableNode<N>> c) {
        this.nodes.sort(c);
    }
}
