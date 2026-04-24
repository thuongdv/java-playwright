package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads test configuration from a .properties file.
 * System properties (set via Maven -D flags) always override file values.
 *
 * <p>Resolution order (highest → lowest priority):
 * <ol>
 *   <li>JVM system property  (-Dbrowser=edge)</li>
 *   <li>Properties file      (config/default.properties or -Dconfig.file=...)</li>
 *   <li>Hard-coded default   (fallback inside {@link #get(String, String)})</li>
 * </ol>
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final ConfigManager INSTANCE = new ConfigManager();

    private final Properties props = new Properties();

    private ConfigManager() {
        String configFile = System.getProperty("config.file", "config/default.properties");
        loadFromFile(configFile);
        log.info("Configuration loaded from: {}", configFile);
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Typed accessors
    // ──────────────────────────────────────────────────────────────────────────

    public String get(String key) {
        return get(key, null);
    }

    /**
     * Returns the value for {@code key}, giving priority to a JVM system property.
     */
    public String get(String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            return sysProp;
        }
        return props.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(val);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            log.warn("Invalid int for key '{}', using default {}", key, defaultValue);
            return defaultValue;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Convenience shortcuts
    // ──────────────────────────────────────────────────────────────────────────

    public String getBrowser()       { return get("browser", "chrome"); }
    public boolean isHeadless()      { return getBoolean("headless", true); }
    public String getBaseUrl()       { return get("base.url", "https://example.com"); }
    public int getDefaultTimeout()   { return getInt("timeout.default", 30_000); }
    public int getNavigationTimeout(){ return getInt("timeout.navigation", 60_000); }
    public int getViewportWidth()    { return getInt("viewport.width", 1280); }
    public int getViewportHeight()   { return getInt("viewport.height", 720); }
    public boolean screenshotOnFailure() { return getBoolean("screenshot.on.failure", true); }
    public String getVideoMode()     { return get("video.mode", "retain-on-failure"); }
    public String getTraceMode()     { return get("trace.mode", "retain-on-failure"); }
    public String getOutputDir()     { return get("output.dir", "target/test-output"); }

    // ──────────────────────────────────────────────────────────────────────────
    // Internals
    // ──────────────────────────────────────────────────────────────────────────

    private void loadFromFile(String filePath) {
        // Try classpath first, then filesystem
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            try {
                is = new FileInputStream(Paths.get(filePath).toFile());
            } catch (IOException e) {
                log.warn("Config file '{}' not found on classpath or filesystem — using defaults / system properties only.", filePath);
                return;
            }
        }
        try (InputStream stream = is) {
            props.load(stream);
        } catch (IOException e) {
            log.error("Failed to load config file '{}'", filePath, e);
        }
    }
}
