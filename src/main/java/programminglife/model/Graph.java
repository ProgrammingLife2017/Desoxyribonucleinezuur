package programminglife.model;


import java.util.Collection;

public interface Graph<N extends Node, E extends Edge> extends Iterable<N> {
    void addAll(Collection<N> nodes);

    Collection<N> getNodes();
}
