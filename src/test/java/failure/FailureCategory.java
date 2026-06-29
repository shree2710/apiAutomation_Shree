package failure;

/**
 * Classification of a test failure, with the engineering action it implies.
 *
 * Maps the three buckets the assignment calls for - test issue, environment
 * issue, application defect - onto fix / workaround / escalation guidance.
 * Mirrors the enum-with-fields style of {@code enums.OrderStatus}.
 */
public enum FailureCategory {

    TEST_ISSUE(
            "Automation/test defect (bad locator, wrong expectation, framework or config error)",
            "FIX: correct the test or framework code - this is not an application bug."),

    ENVIRONMENT_ISSUE(
            "Environment/infrastructure problem (host down, timeout, DNS, network)",
            "WORKAROUND: retry once services are healthy; verify environment before escalating."),

    APPLICATION_DEFECT(
            "Application returned an incorrect result (assertion on a real response failed)",
            "ESCALATE: raise a defect to the dev team with request/response evidence."),

    UNKNOWN(
            "Could not be classified automatically",
            "TRIAGE: inspect the stack trace manually and reclassify.");

    private final String description;
    private final String recommendedAction;

    FailureCategory(String description, String recommendedAction) {
        this.description = description;
        this.recommendedAction = recommendedAction;
    }

    public String description() {
        return description;
    }

    public String recommendedAction() {
        return recommendedAction;
    }
}
