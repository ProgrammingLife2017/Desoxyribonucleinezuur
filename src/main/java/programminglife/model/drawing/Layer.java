package programminglife.model.drawing;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ivo on 2017-05-23.
 */
public class Layer implements Iterable<DrawableNode> {
    private int width;
    private List<DrawableNode> nodes;

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

    public void add(DrawableNode node) {
        this.nodes.add(node);
    }

    @NotNull
    @Override
    public Iterator<DrawableNode> iterator() {
        return nodes.iterator();
    }
}
