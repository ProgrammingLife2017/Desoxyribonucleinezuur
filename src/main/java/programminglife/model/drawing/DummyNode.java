package programminglife.model.drawing;

import programminglife.model.XYCoordinate;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Ivo on 2017-05-23.
 */
public class DummyNode implements DrawableNode {
    private XYCoordinate loc;

    public DummyNode() {
        this.loc = new XYCoordinate(0,0);
    }

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
