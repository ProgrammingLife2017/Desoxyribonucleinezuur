package programminglife.model.drawing;

import programminglife.model.*;

/**
 * An {@link Edge} that also implements {@link Drawable}.
 * @param <D> The type of {@link DrawableNode DrawableNodes} this edge connects.
 */
public interface DrawableEdge<D extends DrawableNode<?, D>> extends Edge<D>, Drawable {

}
