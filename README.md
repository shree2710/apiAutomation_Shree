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

Secrets are best supplied via env vars or `-D` rather than committed.

## Running tests

```bash
# Everything (needs local json-server + private auth server for some tests)
mvn test

# Layer-scoped suites
mvn test -Dsurefire.suiteXmlFiles=testng-api.xml
mvn test -Dsurefire.suiteXmlFiles=testng-ui.xml

# CI-safe subset (offline + public API + UI)
mvn test -Dsurefire.suiteXmlFiles=testng-ci.xml

# Override config at runtime
mvn test -Dsurefire.suiteXmlFiles=testng-ui.xml -Dui.headless=false -Dui.browser=firefox
```

### Tests that need local services
- `PostAPITest`, `JsonSchemaValidation` — require a json-server on `:3000`.
- `LoginAPITest` — requires the private auth server.

These are excluded from the CI suite.

## CI

`.github/workflows/ci.yml` runs on push / PR to `main`/`master`:
1. **build** — `mvn clean test-compile` (compile gate for all sources).
2. **test** — runs `testng-ci.xml` on `ubuntu-latest` (Chrome preinstalled),
   uploading Surefire reports as an artifact.
