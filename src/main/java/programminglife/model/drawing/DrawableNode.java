package programminglife.model.drawing;

import javafx.scene.shape.Rectangle;
import programminglife.model.GenomeGraph;
import programminglife.model.Link;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * Created by toinehartman on 12/06/2017.
 */
public abstract class DrawableNode extends Rectangle implements Drawable {
    private final GenomeGraph graph;
    private final int id;

    private boolean drawDimensionsUpToDate = false;

    DrawableNode(GenomeGraph graph, int id) {
        this.graph = graph;
        this.id = id;
    }

    final public int getIdentifier() {
        return this.id;
    }

    final public GenomeGraph getGraph() {
        return graph;
    }

    final void setDrawDimensionsUpToDate(boolean upToDate) {
        this.drawDimensionsUpToDate = upToDate;
    }

    final boolean isDrawDimensionsUpToDate() {
        return drawDimensionsUpToDate;
    }

    public abstract Collection<Integer> getChildren();

    public abstract Collection<Integer> getParents();

    abstract void replaceParent(DrawableNode oldParent, DrawableNode newParent);
    abstract void replaceChild(DrawableNode oldChild, DrawableNode newChild);

    abstract public String details();

    abstract public int[] getGenomes();

    abstract public void colorize(GenomeGraph graph);

    abstract void setLocation(int x, int y);

    abstract public Link getLink(DrawableNode child);

    abstract protected void setDrawDimensions();

    /**
     * Get a {@link XYCoordinate} representing the size of the Segment.
     * @return The size of the Segment
     */
    public XYCoordinate getSize() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return new XYCoordinate((int) this.getWidth(), (int) this.getHeight());
    }

    /**
     * getter for the center of the left border.
     * @return XYCoordinate.
     */
    public XYCoordinate getLeftBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getCenter().add(-(this.getSize().getX() / 2), 0);
    }

    /**
     * getter for the center.
     * @return XYCoordinate.
     */
    private XYCoordinate getCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getLocation().add(this.getSize().multiply(0.5));
    }

    /**
     * getter for the center of the right border.
     * @return XYCoordinate.
     */
    public XYCoordinate getRightBorderCenter() {
        if (!drawDimensionsUpToDate) {
            setDrawDimensions();
        }
        return this.getCenter().add(this.getSize().getX() / 2, 0);
    }

    /**
     * Getter for top left corner of a Segment.
     * @return {@link XYCoordinate} with the values of the top left corner.
     */
    XYCoordinate getLocation() {
        return new XYCoordinate((int) this.getX(), (int) this.getY());
    }
}
