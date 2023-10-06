package pw.mer.letsplay.product;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;

class ProductDeleteTests extends AbstractControllerTests {
    @Test
    void deleteProduct() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();
        var productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(1));

        given().auth().oauth2(adminToken)
                .delete("/products/" + productId)
                .then()
                .statusCode(HTTP_OK);

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        given().auth().oauth2(adminToken)
                .delete("/products/" + productId)
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }
}
