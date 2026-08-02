package com.saucedemo.exceptions;

/**
 * FrameworkException is the root unchecked exception for the Enterprise Selenium Framework.
 *
 * <p>Phase 8 Enterprise Enhancement: Replaces generic {@link RuntimeException} throughout
 * the framework, allowing callers to catch framework errors specifically without catching
 * unrelated runtime problems.
 *
 * <p>SOLID – Single Responsibility: This class exists solely to represent a typed
 * framework-level error, carrying a meaningful message and/or cause.
 *
 * <p>SOLID – Open/Closed: Subclasses ({@link InvalidBrowserException}, etc.) extend this
 * without modifying it, adhering to the open/closed principle.
 *
 * <p>Usage:
 * <pre>
 *   throw new FrameworkException("Failed to initialize configuration: " + path, e);
 * </pre>
 */
public class FrameworkException extends RuntimeException {

    /**
     * Constructs a FrameworkException with a descriptive message.
     *
     * @param message the detail message explaining the failure
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Constructs a FrameworkException with a descriptive message and a root cause.
     *
     * @param message the detail message explaining the failure
     * @param cause   the underlying exception that triggered this error
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
