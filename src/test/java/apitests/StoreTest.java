package apitests;

import api_payloads.Store;
import com.github.javafaker.Faker;
import enums.OrderStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import restapi.OrderEndPoints;

import java.time.Instant;

public class StoreTest {

    private final OrderEndPoints orderApi = new OrderEndPoints();
    private Store payload;

    @BeforeClass
    public void dataSetup() {
        Faker faker = new Faker();
        payload = new Store();
        payload.setId(faker.number().numberBetween(1, 10_000));
        payload.setQuantity(faker.number().numberBetween(1, 10));
        payload.setShipDate(Instant.now().toString());
        payload.setStatus(OrderStatus.random().apiValue());
        payload.setComplete(false);
    }

    @Test(priority = 1)
    public void testPostOrder() {
        Response res = orderApi.createOrder(payload);
        res.then().log().all();
        Assert.assertEquals(res.getStatusCode(), 200);
    }
}
