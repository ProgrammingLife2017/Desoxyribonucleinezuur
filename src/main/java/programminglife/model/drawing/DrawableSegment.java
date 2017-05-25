package programminglife.model.drawing;

import programminglife.model.Segment;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A {@link Segment} that also Implements {@link Drawable}.
 */
public class DrawableSegment implements DrawableNode<Segment, DrawableSegment> {
    private Segment segment;

    /**
     * Create a DrawableSegment from a Segment.
     * @param segment The segment to create this DrawableSegment from.
     */
    public DrawableSegment(Segment segment) {
        this.segment = segment;
    }

    // TODO: implement methods

    @Override
    public Collection<DrawableLink> getChildren() {
        return null;
    }

    @Override
    public Collection<DrawableLink> getParents() {
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
    public DrawableSegment getDrawable() {
        return this;
    }
}
