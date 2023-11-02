package pw.mer.letsplay.product;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;

class ProductAddTests extends AbstractControllerTests {
    @Test
    void addProduct() {
        String adminToken = getAdminToken();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        var testProduct = new TestProductFactory.TestProduct();

        String postId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK).extract().body().asString();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(1));

        when().get("/products/" + postId).then()
                .statusCode(HTTP_OK)
                .body("name", is(testProduct.name),
                        "description", is(testProduct.description),
                        "price", is(testProduct.price.floatValue()));
    }


    /**
     * Tries to add invalid testProduct and checks if response contains 'invalidField'
     */
    void checkInvalidProduct(TestProductFactory.TestProduct testProduct, String adminToken, String invalidField) {
        testProduct.requestAdd(adminToken)
                .statusCode(HTTP_BAD_REQUEST)
                .body(matchesRegex("(?i).*" + invalidField + ".*"));
    }

    @Test
    void addProductInvalidPrice() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.price = null;
        checkInvalidProduct(testProduct, adminToken, "price");

        testProduct.price = -1.0;
        checkInvalidProduct(testProduct, adminToken, "price");
    }

    @Test
    void addProductInvalidName() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.name = null;
        checkInvalidProduct(testProduct, adminToken, "name");

        testProduct.name = "";
        checkInvalidProduct(testProduct, adminToken, "name");

        testProduct.name = "a";
        checkInvalidProduct(testProduct, adminToken, "name");

        testProduct.name = "a".repeat(100);
        checkInvalidProduct(testProduct, adminToken, "name");
    }

    @Test
    void addProductInvalidDescription() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.description = null;
        checkInvalidProduct(testProduct, adminToken, "description");

        testProduct.description = "";
        checkInvalidProduct(testProduct, adminToken, "description");

        testProduct.description = "a";
        checkInvalidProduct(testProduct, adminToken, "description");

        testProduct.description = "a".repeat(1001);
        checkInvalidProduct(testProduct, adminToken, "description");
    }

    @Test
    void NotFound() {
        String adminToken = getAdminToken();

        var req = given().auth().oauth2(adminToken).contentType("application/json").request();

        req.get("/products/12345").then().statusCode(HTTP_NOT_FOUND);

        var testProduct = new TestProductFactory.TestProduct();

        req.body(testProduct).put("/products/12345").then().statusCode(HTTP_NOT_FOUND);

        req.delete("/products/12345").then().statusCode(HTTP_NOT_FOUND);
    }
}