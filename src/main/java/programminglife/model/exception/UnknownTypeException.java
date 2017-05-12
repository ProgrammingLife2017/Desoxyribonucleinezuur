package programminglife.model.exception;

/**
 * A checked {@link Exception} thrown when an unknown type identifier (H/S/L) is read from a GFA file.
 */
public class UnknownTypeException extends Exception {
    /**
     * Default constructor for {@link UnknownTypeException} containing just a message.
     * @param message the message about the unknown identifier.
     */
    public UnknownTypeException(String message) {
        super(message);
    }
}
