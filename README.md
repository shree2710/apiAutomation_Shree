# API + UI Automation Framework

[![CI](https://github.com/shree2710/apiAutomation_Shree/actions/workflows/ci.yml/badge.svg)](https://github.com/shree2710/apiAutomation_Shree/actions/workflows/ci.yml)

A Java automation framework demonstrating intermediate SDET practices across
**API** (REST Assured) and **UI** (Selenium 4) layers, built on shared,
reusable utilities.

## Tech stack

| Concern | Library |
|---|---|
| API testing | REST Assured 5.5.7 |
| UI testing | Selenium 4.27 (built-in Selenium Manager) |
| Test runner | TestNG 7.10 |
| JSON | Jackson 2.20 |
| Test data | JavaFaker |
| Build | Maven (Java 21) |

## Project layout

```
src/main/resources/config.properties   # single source of config (URLs, creds, browser)
src/test/java/
├── utils/            ConfigReader, JsonUtil          # reusable core utilities
├── exceptions/       FrameworkException + subtypes    # custom exception hierarchy
├── enums/            OrderStatus                       # API enum
├── services/         HttpService, BaseService, AuthService   # API abstraction
├── restapi/          OrderEndPoints                    # Petstore CRUD
├── api_payloads/     Store, (Pojo)                     # POJOs
├── apitests/         StoreTest                         # API tests
├── ui/driver/        BrowserType, DriverFactory        # UI core utility
├── ui/pages/         BasePage, LoginPage, ProductsPage # Page Objects
└── uitests/          SauceLoginTest                    # UI tests
```

## Design highlights

- **Reusable utilities used across UI + API**
  - `ConfigReader` — classpath-based, fail-fast config (used by both layers)
  - `JsonUtil` — one shared Jackson `ObjectMapper`
  - `DriverFactory` — thread-safe, config-driven WebDriver lifecycle
- **Abstractions** — `HttpService` interface + abstract `BaseService` (all four
  verbs); `BasePage` centralizes wait-backed UI interactions.
- **Exception hierarchy** — `FrameworkException` → `ConfigException`,
  `PayloadException`, `DriverException`.
- **Enums + streams + collections** — `OrderStatus`, `BrowserType`, and stream
  pipelines in `OrderEndPoints` / `ProductsPage`.

## Configuration

All settings live in `src/main/resources/config.properties` and can be
overridden at runtime. Precedence (highest first): **system property
(`-Dkey=...`) → environment variable → config file**.

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

Secrets are best supplied via env vars or `-D` rather than committed.

**Environment overlays:** passing `-Denv=staging` loads `config-staging.properties`
on top of `config.properties`, overriding endpoint URLs per environment. Files live in
`src/main/resources/config-{qa,staging,prod}.properties`.

## Running tests

```bash
# Everything (needs local json-server + private auth server for some tests)
mvn test

# Suite selection (smoke = fast critical subset, regression = everything)
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml
mvn test -Dsurefire.suiteXmlFiles=testng-regression.xml

# Layer-scoped suites
mvn test -Dsurefire.suiteXmlFiles=testng-api.xml
mvn test -Dsurefire.suiteXmlFiles=testng-ui.xml

# CI-safe subset (offline + public API + UI)
mvn test -Dsurefire.suiteXmlFiles=testng-ci.xml

# Suite + environment + runtime overrides
mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Denv=staging -Dui.headless=false
```

Suites are selected by TestNG **groups**: `smoke` (CI-safe critical checks) and
`regression` (all tests).

### Tests that need local services
- `PostAPITest`, `JsonSchemaValidation` — require a json-server on `:3000`.
- `LoginAPITest` — requires the private auth server.

These are excluded from the CI suite.

## Reporting

- **ExtentReports** HTML dashboard at `target/extent-report/index.html` (via
  `listeners/ExtentReportListener`, auto-attached through ServiceLoader). Failed nodes are
  tagged with their failure category.
- **Surefire** JUnit XML at `target/surefire-reports/` (consumed by the pipelines).
- **`target/failure-analysis.md`** — auto-generated triage table of any failures.

## Failure analysis

Failures are auto-classified as **test / environment / application** issues, with a
recommended fix / workaround / escalation and a 1-working-day SLA. Transient environment
failures are retried automatically (assertion/test failures are not). See
[`FAILURE_ANALYSIS.md`](FAILURE_ANALYSIS.md).

## CI/CD

**GitHub Actions** (`.github/workflows/ci.yml`) runs on push / PR to `main`/`master`:
1. **build** — `mvn clean test-compile` (compile gate for all sources).
2. **test** — runs `testng-ci.xml` on `ubuntu-latest` (Chrome preinstalled),
   uploading Surefire reports as an artifact.

**Jenkins** (`Jenkinsfile`, declarative) — intended as a multibranch pipeline triggered on
commit / PR merge:
- Parameters: **SUITE** (`smoke`/`regression`/`api`/`ui`/`ci`) and **ENVIRONMENT**
  (`qa`/`staging`/`prod`).
- Build failure on test failure (non-zero Maven exit fails the build).
- Publishes JUnit results, the ExtentReport (HTML Publisher), and `failure-analysis.md`.
- Requires Global Tools named `jdk21` / `maven3` and a Chrome-capable agent.
