package programminglife.model.drawing;

import programminglife.model.Genome;
import programminglife.model.Link;

import java.util.Collection;

/**
 * A {@link Link} that also Implements {@link Drawable}.
 */
public class DrawableLink implements DrawableEdge<DrawableSegment> {
    private Link link;

    @Override
    public Collection<Genome> getGenomes() {
        return this.link.getGenomes();
    }

    @Override
    public DrawableSegment getStart() {
        return null;
    }

    @Override
    public DrawableSegment getEnd() {
        return null;
    }

}
