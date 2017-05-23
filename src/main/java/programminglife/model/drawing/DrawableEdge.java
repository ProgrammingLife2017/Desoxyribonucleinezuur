package programminglife.model.drawing;

import programminglife.model.*;

/**
 * An {@link Edge} that also implements {@link Drawable}.
 * @param <N> The type of {@link DrawableNode DrawableNodes} this edge connects.
 */
public interface DrawableEdge<N extends DrawableNode<N>> extends Edge<N>, Drawable {

}
