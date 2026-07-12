package exceptions;

/**
 * Thrown when a payload cannot be serialized to / deserialized from JSON.
 */
public class PayloadException extends FrameworkException {

    public PayloadException(String message) {
        super(message);
    }

    public PayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
