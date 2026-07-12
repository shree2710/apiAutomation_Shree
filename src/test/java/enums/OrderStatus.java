package enums;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Valid order statuses accepted by the Petstore  API.*/

public enum OrderStatus {

    PLACED("placed"),
    APPROVED("approved"),
    DELIVERED("delivered");

    private final String apiValue;

    OrderStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }

    public static List<String> apiValues() {
        return Arrays.stream(values())
                .map(OrderStatus::apiValue)
                .collect(Collectors.toList());
    }

    /** Resolves an API string back to the enum, case-insensitively. */
   /* public static OrderStatus fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(s -> s.apiValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown order status: " + value));
    }

   */
    public static OrderStatus random() {
        OrderStatus[] all = values();
        return all[ThreadLocalRandom.current().nextInt(all.length)];
    }
}
