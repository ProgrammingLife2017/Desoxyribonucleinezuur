package programminglife.model;

import org.apache.commons.lang3.NotImplementedException;
import programminglife.model.drawing.DrawableNode;

import java.util.Collection;

/**
 * An Interface for node objects.
 * @param <N> The subtype of Node. Use like this: {@code class FooNode implements Node<FooNode>}
 */
public interface Node<N extends Node<N>> {
    /**
     * Getter for the id.
     * @return int.
     */
    int getIdentifier();

    /**
     * Get the {@link Collection} of bookmarks for this {@link Genome}.
     * @return all bookmarks
     */
    default Collection<Object> getBookmarks() {
        // TODO create a data structure to store bookmarks/annotation
        throw new NotImplementedException("Node#getBookmarks() is not yet implemented");
    }

    /**
     * Get a {@link DrawableNode} that can represent this node.
     * @param <D> The type of DrawableNodes this method returns.
     * @return a {@link DrawableNode} that can represent this node.
     */
    <D extends DrawableNode<N, D>> D getDrawable();
}
