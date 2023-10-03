package pw.mer.letsplay;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;

public class ProductControllerTests extends AbstractControllerTests {
    @Test
    public void productsShouldBePublic() {
        when().get("/products").then().statusCode(HTTP_OK);
    }

    @Test
    public void adminAddsProduct() {
        String adminToken = getAdminToken();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test name",
                            "description": "Test description",
                            "price": 100.0
                        }
                        """)
                .post("/products/add")
                .then()
                .statusCode(HTTP_OK);

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(1));
    }
}
