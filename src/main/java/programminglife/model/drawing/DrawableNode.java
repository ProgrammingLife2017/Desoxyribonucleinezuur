package programminglife.model.drawing;

import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * Created by Ivo on 2017-05-23.
 */
public interface DrawableNode<N extends DrawableNode<N>> extends Node<N>, Drawable {
    Collection<DrawableEdge<N>> getChildren();

    Collection<DrawableEdge<N>> getParents();

    /**
     * Set the location of this Drawable
     * @param newLoc The new location.
     */
    void setLocation(XYCoordinate newLoc);

    /**
     * Get the location of this Drawable
     * @return the location of this Drawable
     */
    XYCoordinate getLocation();

    int getWidth();

    int getHeight();

    XYCoordinate getDimensions();

    void setDimension(XYCoordinate dimensions);

    void setWidth(int width);

    void setHeight(int height);

}
