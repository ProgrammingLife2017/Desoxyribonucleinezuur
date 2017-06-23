package programminglife.model.drawing;

import javafx.scene.paint.Color;
import programminglife.model.GenomeGraph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A class that handles the creation and usage of dummy nodes.
 */
public class DrawableDummy extends DrawableNode {
    private DrawableNode parent;
    private DrawableNode child;

    private SubGraph subGraph;

    /**
     * Create a dummy node.
     * @param id the ID
     * @param parentNode the dummy's parent
     * @param childNode the dummy's child
     * @param graph the GenomeGraph currently drawn
     * @param subgraph The {@link SubGraph} that this DrawableDummy belongs to.
     */
    DrawableDummy(int id, DrawableNode parentNode, DrawableNode childNode, GenomeGraph graph, SubGraph subgraph) {
        super(graph, id);

        this.parent = parentNode;
        this.child = childNode;

        this.subGraph = subgraph;
    }

    @Override
    public Collection<Integer> getChildren() {
        Collection<Integer> result = new LinkedHashSet<>();
        result.add(child.getIdentifier());
        return result;
    }

    @Override
    public Collection<Integer> getParents() {
        Collection<Integer> result = new LinkedHashSet<>();
        result.add(parent.getIdentifier());
        return result;
    }

    @Override
    public void replaceParent(DrawableNode oldParent, DrawableNode newParent) {
        if (this.parent.getIdentifier() == oldParent.getIdentifier()) {
            this.parent = newParent;
        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a parent of this node (%d).",
                    oldParent.getIdentifier(), this.getIdentifier()));
        }
    }

    @Override
    public void replaceChild(DrawableNode oldChild, DrawableNode newChild) {
        if (this.child.getIdentifier() == oldChild.getIdentifier()) {
            this.child = newChild;

        } else {
            throw new NoSuchElementException(
                    String.format("The node to be replaced (%d) is not a child of this node (%d).",
                            oldChild.getIdentifier(), this.getIdentifier()));
        }
    }

    /**
     * Information {@link String} about this.
     *
     * @return info
     */
    @Override
    public String details() {
        return toString();
    }

    @Override
    public DrawableSNP createSNPIfPossible(SubGraph subGraph) {
        return null;
    }

    @Override
    public void colorize(SubGraph sg, double zoomLevel) {
        double genomeFraction = 0.d;
        Map<DrawableNode, Collection<Integer>> from = sg.getGenomes().get(this.getParentSegment());
        if (from != null) {
            Collection<Integer> genomes = from.get(this.getChildSegment());
            if (genomes != null) {
                genomeFraction = genomes.size() / (double) sg.getNumberOfGenomes();
            }
        }

        double minStrokeWidth = 1.d, maxStrokeWidth = 6.5;
        double strokeWidth = minStrokeWidth + genomeFraction * (maxStrokeWidth - minStrokeWidth);

        double minBrightness = 0.6, maxBrightness = 0.25;
        double brightness = minBrightness + genomeFraction * (maxBrightness - minBrightness);

        Color strokeColor = Color.hsb(0.d, 0.d, brightness);

        this.setStrokeWidth(strokeWidth * zoomLevel);
        this.setStrokeColor(strokeColor);

    }

    @Override
    public Collection<Integer> getGenomes() {
        Map<DrawableNode, Collection<Integer>> from = subGraph.getGenomes().get(this.getParentSegment());
        if (from != null) {
            Collection<Integer> genomes = from.get(this.getChildSegment());
            if (genomes != null) {
                return genomes;
            }
        }

        return null;
    }

    /**
     * To string method of a dummy.
     * @return Dummy representation of a string.
     */
    @Override
    public String toString() {
        return String.format("Link from %s to %s", this.parent, this.child);
    }

    @Override
    public DrawableNode getParentSegment() {
        return this.parent.getParentSegment();
    }

    @Override
    public DrawableNode getChildSegment() {
        return this.child.getChildSegment();
    }

    @Override
    public Collection<Integer> getParentGenomes() {
        return parent.getParentGenomes();
    }

    @Override
    public Collection<Integer> getChildGenomes() {
        return child.getChildGenomes();
    }

    /**
     * Set the size of this drawing.
     */
    @Override
    protected void setDrawDimensions(double zoomLevel) {
    }
}
