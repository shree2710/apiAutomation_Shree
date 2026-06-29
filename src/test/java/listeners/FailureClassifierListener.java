package listeners;

import failure.FailureCategory;
import failure.FailureClassifier;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * On every failure, classifies it (test / environment / application) and prints
 * the recommended action. At the end of the run it writes
 * {@code target/failure-analysis.md} - a triage table the pipeline publishes.
 *
 * Registered via ServiceLoader ({@code META-INF/services/org.testng.ITestNGListener}).
 */
public class FailureClassifierListener implements ITestListener {

    private final List<String> rows = new ArrayList<>();

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable error = result.getThrowable();
        FailureCategory category = FailureClassifier.classify(error);
        String test = testName(result);
        String cause = (error == null)
                ? "n/a"
                : error.getClass().getSimpleName() + ": " + summarize(error.getMessage());

        System.out.printf(
                "%n[FAILURE-ANALYSIS] %s%n  category : %s (%s)%n  cause    : %s%n  action   : %s%n",
                test, category, category.description(), cause, category.recommendedAction());

        rows.add("| " + test + " | " + category + " | " + cause + " | " + category.recommendedAction() + " |");
    }

    @Override
    public void onFinish(ITestContext context) {
        if (rows.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder()
                .append("# Failure Analysis\n\n")
                .append("Auto-generated for suite **").append(context.getSuite().getName())
                .append("** (env: ").append(System.getProperty("env", "qa")).append(").\n\n")
                .append("| Test | Category | Cause | Recommended action |\n")
                .append("|------|----------|-------|--------------------|\n");
        rows.forEach(r -> sb.append(r).append("\n"));

        try {
            Path out = Path.of("target", "failure-analysis.md");
            Files.createDirectories(out.getParent());
            Files.writeString(out, sb.toString());
            System.out.println("[FAILURE-ANALYSIS] wrote " + out.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[FAILURE-ANALYSIS] could not write report: " + e.getMessage());
        }
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private static String summarize(String msg) {
        if (msg == null) {
            return "";
        }
        String oneLine = msg.replaceAll("\\s+", " ").trim();
        return oneLine.length() > 120 ? oneLine.substring(0, 117) + "..." : oneLine;
    }
}
