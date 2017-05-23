package programminglife.model.drawing;

import programminglife.model.Segment;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * Created by Ivo on 2017-05-23.
 */
public class DrawableSegment implements DrawableNode<DrawableSegment> {

    private Segment segment;

    public DrawableSegment(Segment segment) {
        this.segment = segment;
    }

    @Override
    public Collection<DrawableEdge<DrawableSegment>> getChildren() {
        return null;
    }

    @Override
    public Collection<DrawableEdge<DrawableSegment>> getParents() {
        return null;
    }

    @Override
    public void setLocation(XYCoordinate newLoc) {

    }

    @Override
    public XYCoordinate getLocation() {
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

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public Collection<Object> getBookmarks() {
        return null;
    }
}
