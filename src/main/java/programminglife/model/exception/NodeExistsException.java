package programminglife.model.exception;

/**
 * Exceptions class for NodeExists.
 */
public class NodeExistsException extends RuntimeException {
    /**
     * Constructor with super.
     * @param message String containing the message.
     */
    public NodeExistsException(String message) {
        super(message);
    }
}
