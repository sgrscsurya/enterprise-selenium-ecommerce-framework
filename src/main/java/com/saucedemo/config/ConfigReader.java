package com.saucedemo.config;

/**
 * ConfigReader provides a static facade over EnvironmentManager.
 *
 * <p>Phase 7 Enhancement: All property resolution is now delegated to
 * {@link EnvironmentManager}, which supports multi-environment configuration
 * (local / dev / qa / uat / staging / prod) via the {@code -Denv} JVM property.
 *
 * <p>Phase 8 Enhancement: {@link EnvironmentManager} is now a Singleton (Design Pattern)
 * — configuration is parsed exactly once per JVM and reused for every property lookup.
 *
 * <p>Backward Compatibility: All existing call sites (e.g., {@code ConfigReader.getProperty("url")})
 * continue to work without modification.
 */
public class ConfigReader {

    private ConfigReader() {
        // Prevent instantiation – all methods are static delegates
    }

    /**
     * Gets a property value by string key from the active environment config.
     *
     * @param key Property key name
     * @return Trimmed string property value
     * @throws com.saucedemo.exceptions.FrameworkException if key is not found
     */
    public static String getProperty(String key) {
        return EnvironmentManager.getProperty(key);
    }

    /**
     * Gets an integer property value from the active environment config.
     *
     * @param key Property key name
     * @return Integer value
     */
    public static int getIntProperty(String key) {
        return EnvironmentManager.getIntProperty(key);
    }

    /**
     * Gets a boolean property value from the active environment config.
     *
     * @param key Property key name
     * @return Boolean value
     */
    public static boolean getBooleanProperty(String key) {
        return EnvironmentManager.getBooleanProperty(key);
    }

    /**
     * Returns the name of the currently active environment.
     *
     * @return Active environment name (e.g. "local", "staging", "prod")
     */
    public static String getActiveEnvironment() {
        return EnvironmentManager.getActiveEnvironment();
    }
}
