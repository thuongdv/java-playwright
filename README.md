# Playwright + Java + TestNG + Maven Framework

A cross-browser test automation framework using Playwright (Java), TestNG, and Maven.

---

## Project Structure

```
java-playwright/
├── config/
│   ├── default.properties      # Default config (chrome, headless)
│   └── staging.properties      # Staging environment config
├── src/test/
│   ├── java/com/example/
│   │   ├── pages/
│   │   │   └── HomePage.java   # Page Object
│   │   ├── tests/
│   │   │   ├── BaseTest.java   # TestNG lifecycle: setup / teardown
│   │   │   └── HomePageTest.java
│   │   └── utils/
│   │       ├── BrowserFactory.java  # Launches the right browser
│   │       └── ConfigManager.java   # Reads properties + CLI overrides
│   └── resources/
│       ├── testng.xml
│       └── logback-test.xml
└── pom.xml
```

---

## Prerequisites

- Java 21+
- Maven 3.8+
- Playwright browsers installed:

```bash
./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

---

## Running Tests

### Default (Chrome, headless)
```bash
./mvn test
```

### Choose browser
```bash
./mvn test -Dbrowser=chrome      # Google Chrome (default)
./mvn test -Dbrowser=chromium    # Playwright-bundled Chromium
./mvn test -Dbrowser=edge        # Microsoft Edge
```

### Headless / headed mode
```bash
./mvn test -Dheadless=false      # visible browser window
./mvn test -Dheadless=true       # headless (default)
```

### Use an environment-specific config file
```bash
./mvn test -Dconfig.file=config/staging.properties
```

### Combine flags
```bash
./mvn test -Dbrowser=edge -Dheadless=false -Dconfig.file=config/staging.properties
```

---

## Configuration Priority

System properties (`-D` flags) always **override** values in the config file.

| Priority | Source |
|----------|--------|
| 1 (highest) | `-Dbrowser=edge` (CLI / Maven) |
| 2 | `config/default.properties` (or the file set via `-Dconfig.file`) |
| 3 (lowest) | Hard-coded defaults in `ConfigManager` |

---

## Supported Browsers

| Value     | Engine              | Notes                        |
|-----------|---------------------|------------------------------|
| `chrome`  | Chromium (channel)  | Requires Chrome installed    |
| `chromium`| Bundled Chromium    | Always available via Playwright |
| `edge`    | Chromium (channel)  | Requires Edge installed      |

---

## Artifacts on Failure

When a test fails, the framework automatically saves:

- **Screenshot** → `target/test-output/screenshots/`
- **Playwright Trace** → `target/test-output/traces/` (open with `npx playwright show-trace <file>`)
- **Video** → `target/test-output/videos/` (when `video.mode=retain-on-failure`)

---

## CI — Browser Matrix Example (GitHub Actions)

```yaml
strategy:
  matrix:
    browser: [chrome, chromium, edge]
steps:
  - run: mvn test -Dbrowser=${{ matrix.browser }}
```
