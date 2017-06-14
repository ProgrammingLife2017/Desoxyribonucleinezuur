package programminglife.model.exception;

/**
 * A Feature within a Genome.
 */
public class Feature {
    private final String id;
    private final String file;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        return id.equals(feature.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
