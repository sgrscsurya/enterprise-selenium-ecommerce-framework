package com.saucedemo.config;

import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.exceptions.FrameworkException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * EnvironmentManager resolves and loads the active environment configuration.
 *
 * <p>Phase 8 Enterprise Enhancement: Implemented as a lazily-initialized, thread-safe
 * Singleton (Design Pattern – Singleton). All properties are loaded exactly once per JVM
 * into an instance-level {@link Properties} object owned by {@link #instance}.
 *
 * <p>Supports multi-environment execution by reading a JVM system property or environment
 * variable named {@code env} (e.g., "dev", "qa", "uat", "staging", "prod").
 *
 * <p>Resolution Order:
 * <ol>
 *   <li>JVM System Property: {@code -Denv=qa} (highest priority)
 *   <li>Environment Variable: {@code ENV=qa}
 *   <li>Default: {@code FrameworkConstants.ENV_LOCAL} (uses {@code config.properties})
 * </ol>
 *
 * <p>Config file mapping:
 * <ul>
 *   <li>{@code local}   → {@code config.properties}
 *   <li>{@code dev}     → {@code config-dev.properties}
 *   <li>{@code qa}      → {@code config-qa.properties}
 *   <li>{@code uat}     → {@code config-uat.properties}
 *   <li>{@code staging} → {@code config-staging.properties}
 *   <li>{@code prod}    → {@code config-prod.properties}
 * </ul>
 *
 * <p>Usage in CI/CD:
 * <pre>
 *   mvn test -Denv=qa -Dbrowser=chrome
 * </pre>
 *
 * <p>Backward Compatibility: {@link ConfigReader} and all existing call sites continue to use
 * the static delegate methods below; only the internal implementation is now Singleton-backed.
 */
public final class EnvironmentManager {

    private static volatile EnvironmentManager instance;

    private final Properties resolvedProperties = new Properties();
    private final String activeEnvironment;

    /**
     * Private constructor – loads configuration exactly once when the Singleton
     * instance is first created. Enforces the Singleton pattern's core invariant.
     */
    private EnvironmentManager() {
        this.activeEnvironment = resolveEnvironmentName();
        loadEnvironmentConfig(this.activeEnvironment, this.resolvedProperties);
    }

    /**
     * Returns the single shared instance, creating it on first access (thread-safe
     * double-checked locking).
     *
     * @return the Singleton EnvironmentManager instance
     */
    public static EnvironmentManager getInstance() {
        EnvironmentManager result = instance;
        if (result == null) {
            synchronized (EnvironmentManager.class) {
                result = instance;
                if (result == null) {
                    instance = result = new EnvironmentManager();
                }
            }
        }
        return result;
    }

    /**
     * Resolves which environment name is active for this run.
     */
    private static String resolveEnvironmentName() {
        String env = System.getProperty("env");
        if (env == null || env.isBlank()) {
            env = System.getenv("ENV");
        }
        if (env == null || env.isBlank()) {
            env = FrameworkConstants.ENV_LOCAL;
        }
        return env.toLowerCase().trim();
    }

    /**
     * Loads the correct properties file based on the resolved environment name.
     * Falls back to classpath resolution if the file cannot be found relative to
     * the working directory (e.g., when running from a packaged jar).
     * Override values from JVM system properties are applied AFTER file load,
     * allowing CLI flags like {@code -Dbrowser=firefox} to win.
     */
    private static void loadEnvironmentConfig(String activeEnvironment, Properties targetProperties) {
        String configFileName = activeEnvironment.equals(FrameworkConstants.ENV_LOCAL)
                ? "config.properties"
                : "config-" + activeEnvironment + ".properties";

        String configPath = FrameworkConstants.CONFIG_DIR + configFileName;

        try (FileInputStream fis = new FileInputStream(configPath)) {
            targetProperties.load(fis);
        } catch (IOException fileNotFoundOnDisk) {
            // Fallback: resolve from classpath (covers packaged jar / non-project-root CWD execution)
            try (InputStream classpathStream =
                         EnvironmentManager.class.getClassLoader().getResourceAsStream(configFileName)) {
                if (classpathStream == null) {
                    throw new FrameworkException(
                            "Failed to load environment config [" + activeEnvironment + "]. Tried disk path: "
                                    + configPath + " and classpath resource: " + configFileName, fileNotFoundOnDisk);
                }
                targetProperties.load(classpathStream);
            } catch (IOException classpathFailure) {
                throw new FrameworkException(
                        "Failed to load environment config [" + activeEnvironment + "] from classpath: "
                                + configFileName, classpathFailure);
            }
        }

        // Allow JVM system properties to override any config value.
        // Critical for CI/CD override of individual keys (e.g., -Dbrowser=firefox).
        for (String key : targetProperties.stringPropertyNames()) {
            String override = System.getProperty(key);
            if (override != null && !override.isBlank()) {
                targetProperties.setProperty(key, override.trim());
            }
        }
    }

    /**
     * Retrieves a configuration property value.
     *
     * @param key the property key
     * @return the trimmed string value
     * @throws FrameworkException if the key does not exist
     */
    public String getPropertyValue(String key) {
        String value = resolvedProperties.getProperty(key);
        if (value == null) {
            throw new FrameworkException(
                    "Property '" + key + "' not found in config for environment: " + activeEnvironment);
        }
        return value.trim();
    }

    /** Retrieves an integer configuration property value. */
    public int getIntPropertyValue(String key) {
        return Integer.parseInt(getPropertyValue(key));
    }

    /** Retrieves a boolean configuration property value. */
    public boolean getBooleanPropertyValue(String key) {
        return Boolean.parseBoolean(getPropertyValue(key));
    }

    /** Returns the name of the currently active environment. */
    public String getActiveEnvironmentName() {
        return activeEnvironment;
    }

    // ========================================================================
    // Static delegate methods – preserved for backward compatibility with all
    // existing call sites (e.g., ConfigReader.getProperty("url")).
    // ========================================================================

    public static String getProperty(String key) {
        return getInstance().getPropertyValue(key);
    }

    public static int getIntProperty(String key) {
        return getInstance().getIntPropertyValue(key);
    }

    public static boolean getBooleanProperty(String key) {
        return getInstance().getBooleanPropertyValue(key);
    }

    public static String getActiveEnvironment() {
        return getInstance().getActiveEnvironmentName();
    }
}
