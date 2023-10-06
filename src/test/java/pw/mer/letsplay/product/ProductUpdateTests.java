package pw.mer.letsplay.product;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.matchesRegex;

class ProductUpdateTests extends AbstractControllerTests {
    static void updateProductRequestValid(String adminToken, String productId, TestProductFactory.TestProduct testProduct) {
        given().auth().oauth2(adminToken).contentType("application/json")
                .when()
                .body(testProduct)
                .put("/products/" + productId)
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    void updateWholeProductValid() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        var newTestProduct = new TestProductFactory.TestProduct();
        updateProductRequestValid(adminToken, productId, newTestProduct);
    }

    @Test
    void updateProductFieldsValid() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        var emptyProduct = TestProductFactory.TestProduct.Empty();

        updateProductRequestValid(adminToken, productId, emptyProduct.toBuilder().name("New name").build());
        testProduct.name = "New name";

        testProduct.requestCheck(adminToken, productId);


        updateProductRequestValid(adminToken, productId, emptyProduct.toBuilder().description("New description").build());
        testProduct.description = "New description";

        testProduct.requestCheck(adminToken, productId);

        updateProductRequestValid(adminToken, productId, emptyProduct.toBuilder().price(100.0).build());
        testProduct.price = 100.0;

        testProduct.requestCheck(adminToken, productId);
    }

    static void updateProductRequestInvalid(TestProductFactory.TestProduct testProduct, String adminToken, String invalidField) {
        given().auth().oauth2(adminToken).contentType("application/json")
                .when()
                .body(testProduct)
                .post("/products/add")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body(matchesRegex("(?i).*" + invalidField + ".*"));
    }

    @Test
    void updateProductInvalidName() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();
        testProduct.requestAdd(adminToken).statusCode(HTTP_OK);

        var emptyProduct = TestProductFactory.TestProduct.Empty();

        updateProductRequestInvalid(emptyProduct.toBuilder().name(null).build(), adminToken, "name");

        updateProductRequestInvalid(emptyProduct.toBuilder().name("").build(), adminToken, "name");

        updateProductRequestInvalid(emptyProduct.toBuilder().name("a").build(), adminToken, "name");

        updateProductRequestInvalid(emptyProduct.toBuilder().name("a".repeat(100)).build(), adminToken, "name");
    }

    @Test
    void updateProductInvalidPrice() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();
        testProduct.requestAdd(adminToken).statusCode(HTTP_OK);

        var emptyProduct = TestProductFactory.TestProduct.Empty();

        updateProductRequestInvalid(emptyProduct.toBuilder().price(null).build(), adminToken, "price");

        updateProductRequestInvalid(emptyProduct.toBuilder().price(-1.0).build(), adminToken, "price");
    }
}
