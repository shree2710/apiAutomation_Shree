package restapi;

import api_payloads.Store;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

//method created for performing CURD operations
public class OrderEndPoints {
    public static Response createOrder(Store payload){
        Response r = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(payload)
                .when().post(Routes.post_url);

        return r;
    }
// use of java stream
    public static Response readOder(String orderID){
        List<String> orderIds = Arrays.asList("101", "102", "103");
       /* Response r = given()
                .pathParam("orderId",orderID)
                .when().get(Routes.get_url);*/

        List<Response> responses = orderIds.stream()
                .map(id -> given()
                        .pathParam("orderId", id)
                        .when()
                        .get(Routes.get_url))
                .collect(Collectors.toList());


        return  responses.stream()
                .filter(response -> response.path("orderId").equals(orderID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid orderID: " + orderID));

    }

    public static Response updateOrder(String orderID,Store payload) {
        Response r = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .pathParam("orderId", orderID)
                .body(payload)
                .when().put(Routes.update_url);

        return r;
    }

    public static Response updateOrder(String orderID){
            Response r = given()
                    .pathParam("orderId",orderID)
                    .when()
                    .delete(Routes.delete_url);
            return r;
        }




}
