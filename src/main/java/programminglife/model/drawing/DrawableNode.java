package programminglife.model.drawing;

import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A {@link Node} that implements {@link Drawable}.
 * @param <N> The type of Nodes this DrawableNode represents.
 *           (answers the question: what type of Node can I draw with this DrawableNode?)
 * @param <D> The type of {@link DrawableNode DrawableNodes} this can have as children / parents.
 *
 */
public interface DrawableNode<N extends Node<N>, D extends DrawableNode<N, D>> extends Node<D>, Drawable {
    /**
     * Get the {@link DrawableEdge DrawableEdges} connecting this DrawableNode to its children.
     * @return the {@link DrawableEdge DrawableEdges} connecting this DrawableNode to its children.
     */
    Collection<? extends DrawableEdge<D>> getChildEdges();

    /**
     * Get the {@link DrawableEdge DrawableEdges} connecting this DrawableNode to its parents.
     * @return the {@link DrawableEdge DrawableEdges} connecting this DrawableNode to its parents.
     */
    Collection<? extends DrawableEdge<D>> getParentEdges();

    /**
     * Set the location of this DrawableNode.
     * @param newLoc The new location.
     */
    void setLocation(XYCoordinate newLoc);

    /**
     * Get the location of this DrawableNode.
     * @return the location
     */
    XYCoordinate getLocation();

    /**
     * get the width of this DrawableNode.
     * @return the width of this DrawableNode.
     */
    int getWidth();

    /**
     * get the height of this DrawableNode.
     * @return the height of this DrawableNode.
     */
    int getHeight();

    /**
     * Get the dimensions of this DrawableNode. The dimensions are the width and the height.
     * The width is the X of the {@link XYCoordinate}, and the height is the Y.
     * @return An XYCoordinate representing the dimensions of this DrawableNode.
     */
    default XYCoordinate getDimensions() {
        return new XYCoordinate(getWidth(), getHeight());
    }

    /**
     * Set the dimensions of this DrawableNode. The dimensions are the width and the height.
     * The width is the X of the {@link XYCoordinate}, and the height is the Y.
     * @param dimensions An XYCoordinate representing the dimensions of this DrawableNode.
     */
    default void setDimension(XYCoordinate dimensions) {
        setWidth(dimensions.getX());
        setHeight(dimensions.getY());
    }

    /**
     * Set the width of this DrawableNode.
     * @param width The width of this DrawableNode.
     */
    void setWidth(int width);

    /**
     * Set the height of this DrawableNode.
     * @param height The height of this DrawableNode.
     */
    void setHeight(int height);

}
