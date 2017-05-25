package programminglife.model.drawing;

import programminglife.model.Node;
import programminglife.model.XYCoordinate;

import java.util.Collection;

/**
 * A type of {@link DrawableNode}. It is used by {@link SubGraph#layout()} to lay out the edges better.
 * This is a {@link programminglife.model.Node} for the {@link SubGraph},
 * but not an actual Node within {@link programminglife.model.GenomeGraph}.
 */
public class DummyNode implements DrawableNode<DummyNode.None, DummyNode> {
    private XYCoordinate loc;

    /**
     * Default empty constructor. Set location to (0,0).
     */
    public DummyNode() {
        this.loc = new XYCoordinate(0, 0);
    }

    // TODO: implement the methods.

    @Override
    public void setLocation(XYCoordinate newLoc) {
        this.loc = newLoc;
    }

    @Override
    public XYCoordinate getLocation() {
        return this.loc;
    }

    @Override
    public Collection<DrawableEdge<DummyNode>> getChildren() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public Collection<DrawableEdge<DummyNode>> getParents() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public int getWidth() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public int getHeight() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public XYCoordinate getDimensions() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public void setDimension(XYCoordinate dimensions) {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public void setWidth(int width) {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public void setHeight(int height) {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public int getIdentifier() {
        throw new Error("This method has not yet been implemented");
    }

    @Override
    public DummyNode getDrawable() {
        return this;
    }

    /**
     * Class to represent that DummyNode does not represent any actual nodes.
     */
    static final class None implements Node<None> {

        /**
         * private constructor to prevent creation.
         */
        private None() {
        }

        @Override
        public int getIdentifier() {
            throw new Error("This class should not be used");
        }

        @Override
        public Collection<Object> getBookmarks() {
            throw new Error("This class should not be used");
        }

        @Override
        public DummyNode getDrawable() {
            throw new Error("This class should not be used");
        }
    }
}
