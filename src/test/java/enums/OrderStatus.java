package enums;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Valid order statuses accepted by the Petstore {@code /store/order} API.
 *
 * Replaces the previous magic-string "status" so the set of legal values lives
 * in one place. Demonstrates enums together with collections and streams.
 */
public enum OrderStatus {

    PLACED("placed"),
    APPROVED("approved"),
    DELIVERED("delivered");

    private final String apiValue;

    OrderStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    /** The lowercase string the API actually expects on the wire. */
    public String apiValue() {
        return apiValue;
    }

    /** All wire values, e.g. for logging or building assertions. */
    public static List<String> apiValues() {
        return Arrays.stream(values())
                .map(OrderStatus::apiValue)
                .collect(Collectors.toList());
    }

    /** Resolves an API string back to the enum, case-insensitively. */
    public static OrderStatus fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(s -> s.apiValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown order status: " + value));
    }

    /** Picks a random status - handy for data-driven test setup. */
    public static OrderStatus random() {
        OrderStatus[] all = values();
        return all[ThreadLocalRandom.current().nextInt(all.length)];
    }
}
