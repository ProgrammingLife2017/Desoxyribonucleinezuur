package programminglife.model.drawing;

import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.HashSet;

/**
 * A type of {@link DrawableNode}. It is used by {@link SubGraph#layout()} to lay out the edges better.
 * This is a {@link programminglife.model.Node} for the {@link SubGraph},
 * but not an actual Node within {@link programminglife.model.GenomeGraph}.
 */
public class DummyNode implements DrawableNode {
    private XYCoordinate loc;

    /**
     * Default empty constructor. Set location to (0,0).
     */
    public DummyNode() {
        this.loc = new XYCoordinate(0, 0);
    }

    // TODO: implement the methods.

    @Override
    public int getIdentifier() {
        return -1;
    }

    @Override
    public Collection<Object> getBookmarks() {
        return new HashSet<>();
    }

    @Override
    public void setLocation(XYCoordinate newLoc) {
        this.loc = newLoc;
    }

    @Override
    public XYCoordinate getLocation() {
        return this.loc;
    }

    @Override
    public Collection<DrawableEdge> getChildren() {
        return null;
    }

    @Override
    public Collection<DrawableEdge> getParents() {
        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public XYCoordinate getDimensions() {
        return null;
    }

    @Override
    public void setDimension(XYCoordinate dimensions) {

    }

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setHeight(int height) {

    }
}
