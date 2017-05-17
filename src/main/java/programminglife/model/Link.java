package programminglife.model;

import java.util.Collection;
import java.util.Set;

/**
 * Created by toinehartman on 17/05/2017.
 */
public class Link implements Edge {
    private Set<Genome> genomeSet;

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Genome> getGenomes() {
        return this.genomeSet;
    }
}
