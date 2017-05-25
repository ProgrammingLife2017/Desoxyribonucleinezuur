package programminglife.model.drawing;

import programminglife.model.Genome;
import programminglife.model.Link;
import programminglife.model.Node;

import java.util.Collection;

/**
 * A {@link DrawableEdge} for connecting DummyNodes and normal Nodes.
 * A {@link Link} should be replaced with a path of DummyEdges.
 * @param <D> A type parameter to bind it all together (= implement that DrawableEdge interface).
 *           You should be able to ignore it entirely.
 */
public class DummyEdge<D extends DrawableNode<? extends Node, D>> implements DrawableEdge<D> {
    // TODO: implement

    @Override
    public Collection<Genome> getGenomes() {
        return null;
    }

    @Override
    public D getStart() {
        return null;
    }

    @Override
    public D getEnd() {
        return null;
    }
}
