package com.example.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.pages.HomePage;

/**
 * Example tests that exercise the home page.
 *
 * <p>Run all browsers sequentially:
 * <pre>
 *   mvn test                                         # uses default.properties (chrome)
 *   mvn test -Dbrowser=chromium
 *   mvn test -Dbrowser=edge
 *   mvn test -Dheadless=false                        # visible browser
 *   mvn test -Dconfig.file=config/staging.properties # staging env
 * </pre>
 */
public class HomePageTest extends BaseTest {

    @Test(description = "Page title should not be empty")
    public void testPageTitleIsNotEmpty() {
        HomePage homePage = new HomePage(page);
        homePage.navigate(config.getBaseUrl());

        String title = homePage.getTitle();
        log.info("Page title: '{}'", title);

        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
    }

    @Test(description = "Page URL should match the configured base URL")
    public void testPageUrlMatchesBaseUrl() {
        HomePage homePage = new HomePage(page);
        homePage.navigate(config.getBaseUrl());

        String currentUrl = homePage.getCurrentUrl();
        log.info("Current URL: '{}'", currentUrl);

        Assert.assertTrue(
            currentUrl.startsWith(config.getBaseUrl()),
            "URL mismatch — expected prefix: " + config.getBaseUrl()
        );
    }

    @Test(description = "Browser name should be reflected in the config")
    public void testBrowserConfigIsCorrect() {
        String browser = config.getBrowser();
        log.info("Active browser: '{}'", browser);

        Assert.assertTrue(
            browser.matches("chrome|chromium|edge"),
            "Browser should be one of: chrome, chromium, edge — got: " + browser
        );
    }

    @Test(description = "Page load should complete within the navigation timeout")
    public void testPageLoadsSuccessfully() {
        long start = System.currentTimeMillis();
        page.navigate(config.getBaseUrl());
        long elapsed = System.currentTimeMillis() - start;

        log.info("Page loaded in {} ms (timeout: {} ms)", elapsed, config.getNavigationTimeout());

        Assert.assertTrue(
            elapsed < config.getNavigationTimeout(),
            "Page load exceeded navigation timeout"
        );
    }
}
