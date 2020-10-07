package it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.api.StoreResource;
import ibm.gse.eda.stores.domain.Store;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
@QuarkusTest
@TestHTTPEndpoint(StoreResource.class)
public class TestStoreResource {

    @Test
    public void shouldHaveStore_1_fromGetStoreNames(){
        given().when().get("/names").then().statusCode(200).body(containsString("Store_1"));
    }
    
    @Test
    public void shouldNotHaveStore_7_fromGetStoreNames(){
        given().when().get("/names").then().statusCode(200).body(not(containsString("Store_7")));
    }

    @Test
    public void shouldHaveStores(){
        given().when().get().then().statusCode(200).body(notNullValue(Store.class));
    }

    /**
     * The following code need rabbitmq running with docker compose.
     */
    @Test
    public void shouldStartSendingOneMessage(){
        given().when().post("/start/rmq/1").then().statusCode(201);
    }
}
