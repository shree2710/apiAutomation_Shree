package ui.driver;

import java.util.Arrays;

/**
 * Supported browsers.
 */
public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported browser: " + value));
    }
}
