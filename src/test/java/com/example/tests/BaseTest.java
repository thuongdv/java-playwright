package com.example.tests;

import com.example.utils.BrowserFactory;
import com.example.utils.ConfigManager;
import com.microsoft.playwright.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all tests.
 *
 * <p>Lifecycle per test method:
 *
 * <pre>
 *   &#64;BeforeMethod  → create Playwright, Browser, BrowserContext, Page
 *   @AfterMethod   → handle screenshot / trace / video on failure, then close resources
 * </pre>
 *
 * Subclasses access the {@link Page} via the protected {@code page} field.
 */
public abstract class BaseTest {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  protected final ConfigManager config = ConfigManager.getInstance();

  protected Playwright playwright;
  protected BrowserContext browserContext;
  protected Browser browser;
  protected Page page;

  @BeforeMethod(alwaysRun = true)
  public void setUp(Method testMethod) {
    log.info("▶ Starting test: {}.{}", getClass().getSimpleName(), testMethod.getName());

    playwright = Playwright.create();
    browser = BrowserFactory.createBrowser(playwright);
    browserContext = BrowserFactory.createContext(browser);
    page = browserContext.newPage();
  }

  @AfterMethod(alwaysRun = true)
  public void tearDown(ITestResult result, Method testMethod) {
    String testName = getClass().getSimpleName() + "_" + testMethod.getName();
    boolean failed = result.getStatus() == ITestResult.FAILURE;

    if (failed) {
      log.warn("✘ Test FAILED: {}", testName);
      captureScreenshot(testName);
      saveTrace(testName);
    } else {
      log.info("✔ Test PASSED: {}", testName);
      // Stop tracing without saving for passing tests (retain-on-failure mode)
      if ("retain-on-failure".equals(config.getTraceMode())) {
        try {
          browserContext.tracing().stop();
        } catch (Exception ignored) {
        }
      }
    }

    closeResources();
  }

  private void captureScreenshot(String testName) {
    if (!config.screenshotOnFailure()) return;
    try {
      Path dir = Paths.get(config.getOutputDir(), "screenshots");
      Files.createDirectories(dir);
      Path dest = dir.resolve(testName + "_" + System.currentTimeMillis() + ".png");
      page.screenshot(new Page.ScreenshotOptions().setPath(dest).setFullPage(true));
      log.info("Screenshot saved: {}", dest);
    } catch (Exception e) {
      log.error("Failed to capture screenshot", e);
    }
  }

  private void saveTrace(String testName) {
    String traceMode = config.getTraceMode();
    if ("off".equals(traceMode)) return;
    try {
      Path dir = Paths.get(config.getOutputDir(), "traces");
      Files.createDirectories(dir);
      Path dest = dir.resolve(testName + "_" + System.currentTimeMillis() + ".zip");
      browserContext.tracing().stop(new Tracing.StopOptions().setPath(dest));
      log.info("Trace saved: {}", dest);
    } catch (Exception e) {
      log.error("Failed to save trace", e);
    }
  }

  private void closeResources() {
    try {
      if (browserContext != null) browserContext.close();
    } catch (Exception ignored) {
    }

    try {
      if (browser != null) browser.close();
    } catch (Exception ignored) {
    }

    try {
      if (playwright != null) playwright.close();
    } catch (Exception ignored) {
    }
  }
}
