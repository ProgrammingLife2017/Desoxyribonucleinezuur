package programminglife.model.drawing;

import programminglife.model.Genome;
import programminglife.model.Link;
import programminglife.model.Segment;

import java.util.Collection;

/**
 * Created by Ivo on 2017-05-23.
 */
public class DrawableLink implements DrawableEdge<Segment> {
    private Link link;

    @Override
    public Collection<Genome> getGenomes() {
        return this.link.getGenomes();
    }

    @Override
    public Segment getStart() {
        return this.link.getStart();
    }

    @Override
    public Segment getEnd() {
        return this.link.getEnd();
    }

}
