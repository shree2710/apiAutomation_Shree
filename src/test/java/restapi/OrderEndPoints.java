package restapi;

import api_payloads.Store;
import io.restassured.response.Response;
import services.BaseService;
import utils.ConfigReader;

import java.util.List;


public class OrderEndPoints extends BaseService {

    private static final String ORDER = "/store/order";
    private static final String ORDER_BY_ID = "/store/order/{orderId}";

    public OrderEndPoints() {

        super(ConfigReader.get("petstore.baseUrl"));
    }

    public Response createOrder(Store payload) {

        return post(ORDER, payload);
    }

    public Response getOrder(Long orderId) {
        return request().pathParam("orderId", orderId).when().get(ORDER_BY_ID);
    }

    /** The same GET, deserialized into a {@link Store} with the shared JsonUtil mapper. */
    public Store getOrderPayload(Long orderId) {
        return as(getOrder(orderId), Store.class);
    }

    public Response updateOrder(String orderId, Store payload) {
        return request().pathParam("orderId", orderId).body(payload).when().put(ORDER_BY_ID);
    }

    public Response deleteOrder(String orderId) {
        return request().pathParam("orderId", orderId).when().delete(ORDER_BY_ID);
    }

    /**
     * Java Streams : fetch each id and return the first order that actually
     * exists (HTTP 200), or throw exception if none do.
     */

    public Response findFirstExistingOrder(List<String> orderIds) {
        return orderIds.stream()
                .map(id -> this.getOrder(Long.valueOf(id)))
                .filter(response -> response.getStatusCode() == 200)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existing order found in: " + orderIds));
    }
}
