package pw.mer.letsplay.product;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

class ProductUpdateTests extends AbstractControllerTests {

    private static void updateProductRequestValid(String adminToken, String productId, TestProductFactory.TestProduct testProduct) {
        given().auth().oauth2(adminToken).contentType("application/json")
                .when()
                .body(testProduct)
                .put("/products/" + productId)
                .then()
                .statusCode(HTTP_OK);
    }

    @Test
    void updateWholeProduct() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        var newTestProduct = new TestProductFactory.TestProduct();
        updateProductRequestValid(adminToken, productId, newTestProduct);
    }

    @Test
    void updateProductName() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        var emptyProduct = new TestProductFactory.TestProduct(null, null, null);

        updateProductRequestValid(adminToken, productId, emptyProduct.toBuilder().name("New name").build());

        testProduct.toBuilder().name("New name").build().requestCheck(adminToken, productId);
    }
}
