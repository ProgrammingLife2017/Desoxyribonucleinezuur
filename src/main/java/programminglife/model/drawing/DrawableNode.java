package programminglife.model.drawing;

import javafx.scene.shape.Rectangle;
import programminglife.model.GenomeGraph;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A segment that also implements {@link Drawable}.
 */
public abstract class DrawableNode extends Rectangle implements Drawable {
    static final int NODE_HEIGHT = 10;

    private final GenomeGraph graph;
    private final int id;

    private boolean drawDimensionsUpToDate = false;
    private Collection<Integer> genomes;

    /**
     * Construct a {@link DrawableNode}.
     * @param graph the {@link GenomeGraph} to draw from
     * @param id the ID of the {@link DrawableNode}
     */
    DrawableNode(GenomeGraph graph, int id) {
        this.graph = graph;
        this.id = id;
    }

    /**
     * Get the ID.
     * @return the ID
     */
    public final int getIdentifier() {
        return this.id;
    }

    /**
     * Get the {@link GenomeGraph}.
     * @return the graph
     */
    public final GenomeGraph getGraph() {
        return graph;
    }

    /**
     * Set if the dimensions are up to date.
     * @param upToDate {@link boolean} true if up to date else false
     */
    final void setDrawDimensionsUpToDate(boolean upToDate) {
        this.drawDimensionsUpToDate = upToDate;
    }

    /**
     * Get if the dimensions are up to date.
     * @return boolean true if up to date else false
     */
    final boolean isDrawDimensionsUpToDate() {
        return drawDimensionsUpToDate;
    }

    /**
     * Get the IDs of children of this.
     * @return IDs of drawable children
     */
    public abstract Collection<Integer> getChildren();

    /**
     * Get the IDs of parents of this.
     * @return IDs of drawable parents.
     */
    public abstract Collection<Integer> getParents();

    /**
     * Replace a parent with another one.
     * @param oldParent the parent to replace
     * @param newParent the new parent
     */
    abstract void replaceParent(DrawableNode oldParent, DrawableNode newParent);

    /**
     * Replace child with another one.
     * @param oldChild the child to replace
     * @param newChild the new child
     */
    abstract void replaceChild(DrawableNode oldChild, DrawableNode newChild);

    /**
     * Information {@link String} about this.
     * @return info
     */
    public abstract String details();

    /**
     * Color this according to contents.
     */
    public abstract void colorize();

    /**
     * Set the location to draw this.
     * @param x the x location
     * @param y the y location
     */
    abstract void setLocation(int x, int y);

    /**
     * Set the size of this drawing.
     */
    protected abstract void setDrawDimensions();

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

    @Override
    public final Collection<Integer> getGenomes() {
        return genomes;
    }
}
