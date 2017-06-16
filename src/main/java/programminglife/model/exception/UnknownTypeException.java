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

    /**
     * Constructor for {@link UnknownTypeException} containing a message and a cause.
     * @param message the message about the unknown identifier.
     * @param cause The cause of this Exception.
     */
    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for {@link UnknownTypeException} containing a message and cause.
     * @param message the message about the unknown identifier
     * @param cause the cause of this {@link Exception}
     */
    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
