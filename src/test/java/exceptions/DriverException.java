package exceptions;

/**
 * Thrown when the WebDriver cannot be created or is used before initialization.
 */
public class DriverException extends FrameworkException {

    public DriverException(String message) {
        super(message);
    }

    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
}
