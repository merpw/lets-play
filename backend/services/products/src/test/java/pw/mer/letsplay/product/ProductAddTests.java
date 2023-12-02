package pw.mer.letsplay.product;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

class ProductAddTests extends AbstractControllerTests {
    @Test
    void addProduct() {
        String sellerToken = getSellerToken();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        var testProduct = new TestProductFactory.TestProduct();

        String postId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().body().asString();

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
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.price = null;
        checkInvalidProduct(testProduct, sellerToken, "price");

        testProduct.price = -1.0;
        checkInvalidProduct(testProduct, sellerToken, "price");
    }

    @Test
    void addProductInvalidName() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.name = null;
        checkInvalidProduct(testProduct, sellerToken, "name");

        testProduct.name = "";
        checkInvalidProduct(testProduct, sellerToken, "name");

        testProduct.name = "a";
        checkInvalidProduct(testProduct, sellerToken, "name");

        testProduct.name = "a".repeat(100);
        checkInvalidProduct(testProduct, sellerToken, "name");
    }

    @Test
    void addProductInvalidDescription() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        testProduct.description = null;
        checkInvalidProduct(testProduct, sellerToken, "description");

        testProduct.description = "";
        checkInvalidProduct(testProduct, sellerToken, "description");

        testProduct.description = "a";
        checkInvalidProduct(testProduct, sellerToken, "description");

        testProduct.description = "a".repeat(1001);
        checkInvalidProduct(testProduct, sellerToken, "description");
    }

    @Test
    void NotFound() {
        String sellerToken = getSellerToken();

        authRequest(sellerToken).get("/products/12345").then().statusCode(HTTP_NOT_FOUND);

        var testProduct = new TestProductFactory.TestProduct();

        jsonBodyRequest(sellerToken, testProduct).put("/products/12345").then().statusCode(HTTP_NOT_FOUND);

        authRequest(sellerToken).delete("/products/12345").then().statusCode(HTTP_NOT_FOUND);
    }
}
