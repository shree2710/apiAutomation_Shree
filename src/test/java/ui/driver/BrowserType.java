package ui.driver;

import java.util.Arrays;

/**
 * Supported browsers. Resolved from the {@code ui.browser} config value, so the
 * legal set lives in one place (mirrors how {@code enums.OrderStatus} is used
 * on the API side).
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
