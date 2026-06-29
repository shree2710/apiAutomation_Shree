package listeners;

import failure.FailureCategory;
import failure.FailureClassifier;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.ConfigReader;

/**
 * Retries a failed test - but only when the failure looks transient
 * (an {@link FailureCategory#ENVIRONMENT_ISSUE}). Assertion failures and test
 * bugs are never retried, so we don't mask real defects.
 *
 * Retry budget comes from the {@code retry.count} config key (default 1).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = parseRetries();
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts >= MAX_RETRIES) {
            return false;
        }
        if (FailureClassifier.classify(result.getThrowable()) != FailureCategory.ENVIRONMENT_ISSUE) {
            return false;
        }
        attempts++;
        System.out.printf("[RETRY] %s.%s - transient environment failure, retry %d/%d%n",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName(), attempts, MAX_RETRIES);
        return true;
    }

    private static int parseRetries() {
        try {
            return Math.max(0, Integer.parseInt(ConfigReader.get("retry.count", "1")));
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
