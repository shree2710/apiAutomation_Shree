package apitests;

import api_payloads.Store;
import net.datafaker.Faker;
import enums.OrderStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import restapi.OrderEndPoints;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class StoreTest {

    private final OrderEndPoints orderApi = new OrderEndPoints();
    private Store payload;
    private String existingOrderId;

    @BeforeClass(alwaysRun = true)
    public void dataSetup() {
        Faker faker = new Faker();
        payload = new Store();
        payload.setId(faker.number().numberBetween(1, 10_000));
        payload.setQuantity(faker.number().numberBetween(1, 10));
        payload.setShipDate(Instant.now().toString());
        payload.setStatus(OrderStatus.random().apiValue());
        payload.setComplete(false);
    }

    @Test(priority = 1, groups = {"smoke", "regression"})
    public void testPostOrder() {
        Response res = orderApi.createOrder(payload);
        res.then().log().all();
        Assert.assertEquals(res.getStatusCode(), 200);
    }
    @Test(priority = 2, groups = {"regression"})
    public void testGetOrderById() {
        // Deserialized through the shared JsonUtil mapper, so the assertions run
        // against a typed POJO instead of raw JSON paths.
        Store order = orderApi.getOrderPayload(payload.getId());

        Assert.assertEquals(order.getId(), payload.getId());
        Assert.assertEquals(order.getQuantity(), payload.getQuantity());
        Assert.assertTrue(OrderStatus.apiValues().contains(order.getStatus()),
                "Status '" + order.getStatus() + "' is not one of " + OrderStatus.apiValues());

        existingOrderId = String.valueOf(order.getId());
    }

    @Test(priority = 3, groups = {"regression"})
    public void testFindFirstExistingOrder() {

        List<String> orderIds = Arrays.asList(
                "999999",
                "888888",
                existingOrderId,
                "777777"
        );

        Response response = orderApi.findFirstExistingOrder(orderIds);
        Assert.assertEquals(response.getStatusCode(), 200);
        int returnedId = response.jsonPath().getInt("id");
        Assert.assertEquals(returnedId, Integer.parseInt(existingOrderId));
    }


}
