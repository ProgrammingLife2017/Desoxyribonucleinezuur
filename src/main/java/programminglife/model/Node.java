package programminglife.model;

import java.util.Collection;

/**
 * Created by toinehartman on 17/05/2017.
 */
public interface Node<N extends Node<N>> {
    /**
     * Getter for the id.
     * @return int.
     */
    int getIdentifier();

    Collection<N> getChildren();

    Collection<N> getParents();
}
