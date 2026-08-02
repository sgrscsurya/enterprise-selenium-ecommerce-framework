package com.saucedemo.exceptions;

/**
 * InvalidBrowserException is thrown when an unsupported or unrecognized browser name
 * is passed to {@link com.saucedemo.driver.BrowserFactory} or
 * {@link com.saucedemo.driver.RemoteDriverFactory}.
 *
 * <p>Phase 8 Enterprise Enhancement: Replaces the generic {@link IllegalArgumentException}
 * previously thrown on bad browser values, giving callers a specific, catchable signal.
 *
 * <p>Supported browser values are defined in {@link com.saucedemo.constants.FrameworkConstants}:
 * <ul>
 *   <li>{@code FrameworkConstants.BROWSER_CHROME}  = "chrome"
 *   <li>{@code FrameworkConstants.BROWSER_FIREFOX} = "firefox"
 *   <li>{@code FrameworkConstants.BROWSER_EDGE}    = "edge"
 * </ul>
 *
 * <p>Usage:
 * <pre>
 *   throw new InvalidBrowserException("Unsupported browser: '" + browser
 *       + "'. Valid values: chrome, firefox, edge.");
 * </pre>
 */
public class InvalidBrowserException extends FrameworkException {

    /**
     * Constructs an InvalidBrowserException with a descriptive message
     * that should name the invalid browser value and list valid alternatives.
     *
     * @param message description of which browser was invalid and what is supported
     */
    public InvalidBrowserException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidBrowserException with a message and a root cause.
     *
     * @param message description of the invalid browser error
     * @param cause   the underlying cause (e.g., configuration parse failure)
     */
    public InvalidBrowserException(String message, Throwable cause) {
        super(message, cause);
    }
}
