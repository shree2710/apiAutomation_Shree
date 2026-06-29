package exceptions;

/**
 * Base unchecked exception for the whole automation framework.
 * Every framework-specific exception extends this so callers can catch
 * the entire hierarchy with a single {@code catch (FrameworkException e)}.
 */
public class FrameworkException extends RuntimeException {

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
