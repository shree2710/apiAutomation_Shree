package failure;

import exceptions.ConfigException;
import exceptions.DriverException;
import exceptions.PayloadException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Pure, side-effect-free mapping from a thrown {@link Throwable} to a
 * {@link FailureCategory}. Kept separate from any TestNG listener so it can be
 * unit-tested and reused.
 *
 * The cause chain is walked, so a network failure wrapped by REST Assured (or
 * an assertion wrapped by a runner) is still recognized.
 */
public final class FailureClassifier {

    private FailureClassifier() {
        // utility class - no instances
    }

    public static FailureCategory classify(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            FailureCategory category = classifyOne(t);
            if (category != null) {
                return category;
            }
            if (t.getCause() == t) {
                break; // guard against self-referential cause chains
            }
        }
        return FailureCategory.UNKNOWN;
    }

    private static FailureCategory classifyOne(Throwable t) {
        if (t instanceof ConnectException
                || t instanceof UnknownHostException
                || t instanceof SocketTimeoutException
                || looksLikeNetwork(t)) {
            return FailureCategory.ENVIRONMENT_ISSUE;
        }
        if (t instanceof ConfigException
                || t instanceof PayloadException
                || t instanceof DriverException
                || t instanceof NullPointerException
                || t instanceof IllegalArgumentException) {
            return FailureCategory.TEST_ISSUE;
        }
        if (t instanceof AssertionError) {
            return FailureCategory.APPLICATION_DEFECT;
        }
        return null;
    }

    /** Catches network exception types we don't import directly (Apache HttpClient, etc.). */
    private static boolean looksLikeNetwork(Throwable t) {
        String name = t.getClass().getSimpleName().toLowerCase();
        return name.contains("timeout")
                || name.contains("nohttpresponse")
                || name.contains("connectionpool")
                || name.contains("connectionclosed")
                || name.contains("sslhandshake");
    }
}
