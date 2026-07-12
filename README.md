# API + UI Automation Framework

[![CI](https://github.com/shree2710/apiAutomation_Shree/actions/workflows/ci.yml/badge.svg)](https://github.com/shree2710/apiAutomation_Shree/actions/workflows/ci.yml)

A Java automation framework demonstrating intermediate SDET practices across
**API** (REST Assured) and **UI** (Selenium 4) layers, with shared reusable
utilities, CI/CD pipeline ownership (GitHub Actions + Jenkins), and an automated
failure-analysis framework.

## Contents
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Design highlights](#design-highlights)
- [Configuration & environments](#configuration--environments)
- [Test suites](#test-suites)
- [Running tests](#running-tests)
- [Reporting](#reporting)
- [Failure analysis](#failure-analysis)
- [CI/CD](#cicd)
- [Run it end to end (local)](#run-it-end-to-end-local)

## Tech stack

| Concern | Library |
|---|---|
| API testing | REST Assured 5.5.7 |
| UI testing | Selenium 4.27 (built-in Selenium Manager) |
| Test runner | TestNG 7.10 |
| JSON | Jackson 2.20 |
| Reporting | ExtentReports 5.1 |
| Test data | JavaFaker |
| Build | Maven (Java 21) |

## Project structure

```
pom.xml                         Maven build (Java 21, pinned Surefire)
Jenkinsfile                     Declarative Jenkins pipeline (parameterized)
.github/workflows/ci.yml        GitHub Actions pipeline
FAILURE_ANALYSIS.md             Failure classification framework (Part B)
testng-{smoke,regression,api,ui,ci}.xml   TestNG suites

src/main/resources/
├── config.properties           Base config (URLs, creds, browser, retry)
└── config-{qa,staging,prod}.properties   Per-environment overlays

src/test/java/
├── utils/            ConfigReader, JsonUtil              reusable core utilities
├── exceptions/       FrameworkException + Config/Payload/Driver   exception hierarchy
├── enums/            OrderStatus                          API enum (streams/collections)
├── services/         HttpService, BaseService, AuthService   API abstraction (all verbs)
├── restapi/          OrderEndPoints                       Petstore CRUD service
├── api_payloads/     Store, Pojo                          POJOs
├── apitests/         StoreTest                            API tests
├── ui/driver/        BrowserType, DriverFactory           UI core utility (ThreadLocal driver)
├── ui/pages/         BasePage, LoginPage, ProductsPage    Page Objects
├── uitests/          SauceLoginTest                       UI tests
├── failure/          FailureCategory, FailureClassifier   failure classification (Part B)
├── listeners/        ExtentReportListener,                reporting + classification + retry
│                     FailureClassifierListener,
│                     RetryAnalyzer, RetryTransformer
└── (default pkg)     GetAPITest, PostAPITest, LoginAPITest,
                      JsonSchemaValidation, SerializationDeserialization

src/test/resources/
├── storeSchema.json
└── META-INF/services/org.testng.ITestNGListener   auto-registers the listeners
```

## Design highlights

- **Reusable utilities used across UI + API**
  - `ConfigReader` — classpath-based, fail-fast config with precedence
    *system property → env var → file*, plus per-environment overlays.
  - `JsonUtil` — one shared Jackson `ObjectMapper`.
  - `DriverFactory` — thread-safe, config-driven WebDriver lifecycle.
- **Abstractions** — `HttpService` interface + abstract `BaseService` (all four
  verbs); `BasePage` centralizes wait-backed UI interactions.
- **Exception hierarchy** — `FrameworkException` → `ConfigException`,
  `PayloadException`, `DriverException`.
- **Enums + streams + collections** — `OrderStatus`, `BrowserType`, stream
  pipelines in `OrderEndPoints` / `ProductsPage`.
- **Listeners auto-wired via ServiceLoader** — reporting, failure classification,
  and retry attach to every run with no per-test wiring
  (`META-INF/services/org.testng.ITestNGListener`).

## Configuration & environments

All settings live in `src/main/resources/config.properties` and can be overridden
at runtime. Precedence (highest first): **system property (`-Dkey=...`) →
environment variable → config file**.

| Key | Env var | Purpose |
|---|---|---|
| `petstore.baseUrl` | `PETSTORE_BASEURL` | Petstore base URI |
| `reqres.baseUrl` / `reqres.apiKey` | `REQRES_BASEURL` / `REQRES_APIKEY` | reqres endpoint + key |
| `auth.baseUrl` | `AUTH_BASEURL` | auth server |
| `jsonserver.baseUrl` | `JSONSERVER_BASEURL` | local json-server |
| `ui.baseUrl` | `UI_BASEURL` | UI app under test |
| `ui.browser` | `UI_BROWSER` | `chrome` / `firefox` / `edge` |
| `ui.headless` | `UI_HEADLESS` | `true` / `false` |
| `ui.username` / `ui.password` | `UI_USERNAME` / `UI_PASSWORD` | UI credentials |
| `env` | `ENV` | environment overlay: `qa` / `staging` / `prod` |
| `retry.count` | `RETRY_COUNT` | retries for transient env failures (default 1) |

**Environment overlays:** `-Denv=staging` loads `config-staging.properties` on top
of `config.properties`, overriding endpoint URLs per environment. Overlays live in
`src/main/resources/config-{qa,staging,prod}.properties`. Secrets are best supplied
via env vars or `-D` rather than committed.

## Test suites

Suites are selected with `-Dsurefire.suiteXmlFiles=`. Smoke/regression are selected
by TestNG **groups**; the others are layer- or CI-scoped.

| Suite | File | Contents |
|---|---|---|
| Smoke | `testng-smoke.xml` | Fast, CI-safe critical subset (`smoke` group) |
| Regression | `testng-regression.xml` | All tests (`regression` group) |
| API | `testng-api.xml` | All API-layer tests |
| UI | `testng-ui.xml` | All UI-layer tests |
| CI | `testng-ci.xml` | Offline + public API + UI (clean runner safe) |

## Running tests

```bash
# Everything (needs local json-server + private auth server for some tests)
mvn test

# Suite selection (smoke = fast critical subset, regression = everything)
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml
mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml

# Layer-scoped / CI-safe
mvn test -Dsurefire.suiteXmlFiles=testng-api.xml
mvn test -Dsurefire.suiteXmlFiles=testng-ui.xml
mvn test -Dsurefire.suiteXmlFiles=testng-ci.xml

# Suite + environment + runtime overrides
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Denv=staging -Dui.headless=false -Dui.browser=firefox
```

### Tests that need local services
- `PostAPITest`, `JsonSchemaValidation` — require a json-server on `:3000`.
- `LoginAPITest` — requires the private auth server.

These are excluded from the smoke/CI suites.

## Reporting

- **ExtentReports** HTML dashboard at `target/extent-report/index.html` (via
  `listeners/ExtentReportListener`, auto-attached). Failed nodes are tagged with
  their failure category.
- **Surefire** JUnit XML at `target/surefire-reports/` (consumed by the pipelines).
- **`target/failure-analysis.md`** — auto-generated triage table of any failures.

## Failure analysis

Failures are auto-classified as **test / environment / application** issues, with a
recommended fix / workaround / escalation and a 1-working-day SLA. Transient
environment failures are retried automatically (`retry.count`); assertion and test
failures are never retried, so real defects are not masked. Full framework, decision
tree, and worked examples: [`FAILURE_ANALYSIS.md`](FAILURE_ANALYSIS.md).

## CI/CD

**GitHub Actions** (`.github/workflows/ci.yml`) runs on push / PR to `master`
(the canonical branch):
1. **build** — `mvn clean test-compile` (compile gate for all sources).
2. **test** — runs `testng-ci.xml` on `ubuntu-latest` (Chrome preinstalled),
   uploading Surefire reports as an artifact.

**Jenkins** (`Jenkinsfile`, declarative) — intended as a multibranch pipeline
triggered on commit / PR merge:
- Parameters: **SUITE** (`smoke`/`regression`/`api`/`ui`/`ci`) and **ENVIRONMENT**
  (`qa`/`staging`/`prod`).
- **Build failure on test failure** (non-zero Maven exit fails the build).
- Publishes JUnit results, the ExtentReport (HTML Publisher), and `failure-analysis.md`.
- Requires Global Tools named `jdk21` / `maven3` and a Chrome-capable agent.

**AWS EC2** (`deploy/`) — provision an Amazon Linux 2023 instance that clones
`master`, installs Java 21 / Maven / Chrome, and runs the suite on first boot.
Console-only (no CLI keys). Launch + verification steps: [`deploy/README.md`](deploy/README.md).

## Run it end to end (local)

Verified on macOS (Apple Silicon) with Homebrew. The smoke suite runs fully offline-safe
against public endpoints (Petstore, SauceDemo) plus an offline serialization test, and
drives a real headless Chrome.

```bash
# 1. Install a JDK 21 (no sudo; installs into the Homebrew prefix)
brew install openjdk@21

# 2. Point this shell at it
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"

# 3. Run the exact command the Jenkins "Test" stage runs
mvn -B clean test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Denv=qa -Dui.headless=true

# 4. Open the HTML report
open target/extent-report/index.html
```

Expected: `Tests run: 3, Failures: 0` and `BUILD SUCCESS`. Requires Google Chrome
installed for the UI test (already present on most machines).

**See the failure-analysis path in action** — run a quick suite where something is
broken and inspect `target/failure-analysis.md`; failures are printed with their
category and recommended action, transient environment failures are auto-retried, and
the build exits non-zero (the pipeline quality gate).
