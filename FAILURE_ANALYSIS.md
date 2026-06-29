# Failure Analysis & Engineering Thinking

When a test goes red, the first job is **classification**, not "rerun until green".
Every failure is one of three things — a **test issue**, an **environment issue**, or an
**application defect** — and each implies a different action and owner. This framework is
both documented here and **operationalized in code** (see [How it's automated](#how-its-automated)).

**Resolution SLA: every failure is triaged and given a fix, workaround, or escalation
within 1 working day.**

## Classification

| Category | What it means | Typical signals | Owner | Action |
|---|---|---|---|---|
| **Test issue** | The automation is wrong, not the app | `ConfigException`, `PayloadException`, `DriverException`, `NullPointerException`, bad locator, stale wait, wrong expected value | SDET (us) | **FIX** the test/framework |
| **Environment issue** | Infra/dependency is unhealthy | `ConnectException`, `UnknownHostException`, `SocketTimeoutException`, 502/503, DNS, service down (`localhost:3000`, auth box) | DevOps / SRE | **WORKAROUND**: retry when healthy; fix env |
| **Application defect** | App returned a wrong result | `AssertionError` on a real response (wrong status/body/field), reproducible by hand | Dev team | **ESCALATE** with evidence |

## Triage decision tree

```
Test failed
│
├─ Exception is connection/timeout/DNS/5xx?  ── yes ─▶ ENVIRONMENT ISSUE
│        │ no                                          → verify env health, retry, fix infra
│        ▼
├─ Exception is Config/Payload/Driver/NPE,        ── yes ─▶ TEST ISSUE
│  or a clearly wrong locator/expectation?               → fix the test/framework
│        │ no
│        ▼
├─ Assertion on a real response failed, and       ── yes ─▶ APPLICATION DEFECT
│  reproducible against the app manually?                → escalate to dev with evidence
│        │ no
│        ▼
└─ UNKNOWN ─▶ manual triage, then reclassify
```

## Worked examples (this repo)

| Test | Failure | Category | Why | Action |
|---|---|---|---|---|
| `LoginAPITest.loginTestWithAuth` | `ConnectException` to `64.227.160.186:8080` | Environment | Private auth server unreachable | Workaround: exclude from CI suite; verify box is up; retry |
| `JsonSchemaValidation` / `PostAPITest` | connection refused to `localhost:3000` | Environment | json-server not running | Start the local service; not an app/test bug |
| `GetAPITest.getUsers` | `AssertionError: page expected 2` while service returns 200 | Application defect | API contract changed | Escalate to dev with request/response |
| `StoreTest.testPostOrder` | `AssertionError: status 200 vs 404` | Application defect | Endpoint/contract broke | Escalate with evidence |
| any | `ConfigException: Missing required config key` | Test issue | Config/wiring bug in the framework | Fix config/`ConfigReader` usage |
| `SauceLoginTest` | `TimeoutException` waiting for an element | Test issue (usually) | Bad/stale locator or wait; escalate only if the element genuinely vanished from the app | Fix locator/wait, else escalate |

## Fix / Workaround / Escalation templates

**Fix (test issue)** — root cause in automation; PR with the corrected locator/expectation/
config and a note on what was wrong.

**Workaround (environment issue)** — short-term: retry once the dependency is healthy
(automated for transient cases, see below) and/or quarantine the test from the blocking
suite; long-term: raise an infra ticket to stabilize the environment.

**Escalation (application defect)** — open a defect with: endpoint, request, actual vs
expected response, repro steps, build/commit, and severity. Link the failing run/report.
If it can't be resolved in 1 working day, escalate with **technical justification**:
what's blocked, what was tried, why it's an app defect not a test/env issue.

## How it's automated

The classification above is not just prose — the framework applies it on every run:

- **`failure/FailureClassifier.java`** — pure `classify(Throwable) → FailureCategory`,
  walking the cause chain (so wrapped network/assertion errors are still recognized).
- **`failure/FailureCategory.java`** — the four categories, each carrying its recommended
  fix/workaround/escalation action.
- **`listeners/FailureClassifierListener.java`** — on each failure prints the category +
  action, and writes a triage table to **`target/failure-analysis.md`** for the pipeline
  to publish.
- **`listeners/RetryAnalyzer.java` + `RetryTransformer.java`** — automatically retries
  **only** `ENVIRONMENT_ISSUE` failures (`retry.count`, default 1). Assertion and test
  failures are never retried, so real defects are never masked.
- **`listeners/ExtentReportListener.java`** — tags each failed node in the HTML report
  with its category, keeping the report and the triage consistent.

All listeners auto-attach via ServiceLoader
(`src/test/resources/META-INF/services/org.testng.ITestNGListener`) — no per-test wiring.
