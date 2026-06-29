package apitests;

import api_payloads.Store;
import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import restapi.OrderEndPoints;

public class StoreTest {
    Faker f;
    Store payload;
    @BeforeClass
    public void dataSetup(){
        payload = new Store();
        f = new Faker();
        String status = f.options().option(
                "Placed",
                "Cancelled",
                "In Transit"
        );
        payload.setOrder_id(f.idNumber().hashCode());
        payload.setQuantity(f.number().digit());
        payload.setShip_date(f.date().hashCode());
        payload.setStatus(status);

    }
   @Test(priority=1)
    public void testPostOrder(){
        Response res = OrderEndPoints.createOrder(payload);
        res.then().log().all();
        Assert.assertEquals(res.getStatusCode(),200);
    }
}
