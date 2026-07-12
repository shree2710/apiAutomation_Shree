package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import failure.FailureCategory;
import failure.FailureClassifier;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static final String OUTPUT = "target/extent-report/index.html";

    private ExtentReports extent;

    @Override
    public void onStart(ISuite suite) {
        ExtentSparkReporter spark = new ExtentSparkReporter(OUTPUT);
        spark.config().setReportName("Automation Execution Report");
        spark.config().setDocumentTitle("Execution Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Suite", suite.getName());
        extent.setSystemInfo("Environment", System.getProperty("env", "qa"));
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        node(result).pass("Passed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable t = result.getThrowable();
        if (t == null) {
            node(result).skip("Skipped");
        } else {
            node(result).skip(t);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable error = result.getThrowable();
        FailureCategory category = FailureClassifier.classify(error);

        ExtentTest test = node(result);
        test.assignCategory(category.name());
        test.fail("Classified as **" + category.name() + "** - " + category.recommendedAction());
        if (error != null) {
            test.fail(error);
        }
    }

    private ExtentTest node(ITestResult result) {
        String name = result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
        return extent.createTest(name);
    }
}
