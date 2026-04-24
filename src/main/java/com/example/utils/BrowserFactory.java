package com.example.utils;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Creates and configures a Playwright {@link Browser} instance.
 *
 * <p>Supported browser values (case-insensitive):
 * <ul>
 *   <li>{@code chrome}   — Google Chrome (channel)</li>
 *   <li>{@code chromium} — Playwright-bundled Chromium</li>
 *   <li>{@code edge}     — Microsoft Edge (channel)</li>
 * </ul>
 */
public class BrowserFactory {

    private static final Logger log = LoggerFactory.getLogger(BrowserFactory.class);

    private BrowserFactory() {}

    /**
     * Launches a browser according to the active {@link ConfigManager} settings.
     *
     * @param playwright the Playwright instance for this thread
     * @return a configured {@link Browser}
     */
    public static Browser createBrowser(Playwright playwright) {
        ConfigManager config = ConfigManager.getInstance();
        String browser    = config.getBrowser().toLowerCase().trim();
        boolean headless  = config.isHeadless();

        log.info("Launching browser: '{}' | headless: {}", browser, headless);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(Arrays.asList(
                        "--no-sandbox",
                        "--disable-dev-shm-usage"
                ));

        return switch (browser) {
            case "chrome" -> {
                launchOptions.setChannel("chrome");
                yield playwright.chromium().launch(launchOptions);
            }
            case "edge" -> {
                launchOptions.setChannel("msedge");
                yield playwright.chromium().launch(launchOptions);
            }
            case "chromium" ->
                playwright.chromium().launch(launchOptions);
            default ->
                throw new IllegalArgumentException(
                    "Unsupported browser: '" + browser + "'. Valid values: chrome, chromium, edge");
        };
    }

    /**
     * Creates a {@link BrowserContext} with viewport, video, and trace settings.
     */
    public static BrowserContext createContext(Browser browser) {
        ConfigManager config = ConfigManager.getInstance();
        String outputDir = config.getOutputDir();

        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(config.getViewportWidth(), config.getViewportHeight());

        // Video recording
        switch (config.getVideoMode()) {
            case "on"                -> options.setRecordVideoDir(Paths.get(outputDir, "videos"));
            case "retain-on-failure" -> options.setRecordVideoDir(Paths.get(outputDir, "videos"));
            // "off" → no video dir
        }

        BrowserContext context = browser.newContext(options);

        // Tracing
        switch (config.getTraceMode()) {
            case "on", "retain-on-failure" ->
                context.tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true));
        }

        context.setDefaultTimeout(config.getDefaultTimeout());
        context.setDefaultNavigationTimeout(config.getNavigationTimeout());

        return context;
    }
}
