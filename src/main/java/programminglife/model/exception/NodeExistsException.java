package programminglife.model.exception;

public class NodeExistsException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public NodeExistsException(String message) {
        super(message);
    }
}
