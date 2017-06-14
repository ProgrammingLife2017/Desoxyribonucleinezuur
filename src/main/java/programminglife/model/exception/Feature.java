package programminglife.model.exception;

/**
 * A Feature within a Genome.
 */
public class Feature {
    private final String id = null; // TODO: remove null
    private final String file = null; // TODO: remove null

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Feature feature = (Feature) o;

        return id.equals(feature.id) && file.equals(feature.file);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * file.hashCode();
    }
}
