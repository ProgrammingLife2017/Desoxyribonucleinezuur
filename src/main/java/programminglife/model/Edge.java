package programminglife.model;

import java.util.Collection;

/**
 * Created by Ivo on 2017-05-17.
 */
public interface Edge {
    /**
     * get all the Genomes that go through this Edge.
     * @return The genomes tat go through this edge.
     */
    Collection<Genome> getGenomes();

    

}
