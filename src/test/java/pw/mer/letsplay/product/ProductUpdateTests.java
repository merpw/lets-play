package pw.mer.letsplay.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.matchesRegex;
import static pw.mer.letsplay.RequestHelpers.jsonBodyRequest;

class ProductUpdateTests extends AbstractControllerTests {
    static void updateProductRequestValid(String adminToken, String productId, Object body) {
        jsonBodyRequest(adminToken, body).put("/products/" + productId).then().statusCode(HTTP_OK);
    }

    @Test
    void updateWholeProductValid() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var newTestProduct = new TestProductFactory.TestProduct();
        updateProductRequestValid(adminToken, productId, newTestProduct);
    }

    @Test
    void updateProductFieldsValid() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestValid(adminToken, productId,
                objectMapper.createObjectNode().put("name", "New name")
        );
        testProduct.name = "New name";

        testProduct.requestCheck(adminToken, productId);

        updateProductRequestValid(adminToken, productId,
                objectMapper.createObjectNode().put("description", "New description")
        );
        testProduct.description = "New description";

        testProduct.requestCheck(adminToken, productId);

        updateProductRequestValid(adminToken, productId,
                objectMapper.createObjectNode().put("price", 100.0)
        );
        testProduct.price = 100.0;

        testProduct.requestCheck(adminToken, productId);
    }

    static void updateProductRequestInvalid(String adminToken, String productId, Object body, String errorShouldContain) {
        jsonBodyRequest(adminToken, body)
                .put("/products/" + productId)
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body(matchesRegex("(?i).*" + errorShouldContain + ".*"));
    }

    @Test
    void updateProductInvalidName() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestInvalid(adminToken, productId, objectMapper.createObjectNode().put("name", ""), "name");

        updateProductRequestInvalid(adminToken, productId, objectMapper.createObjectNode().put("name", "a"), "name");

        updateProductRequestInvalid(adminToken, productId, objectMapper.createObjectNode().put("name", "a".repeat(100)), "name");

        var brokenProduct = objectMapper.createObjectNode();
        brokenProduct.putObject("name").put("wow", "object as a name!");

        updateProductRequestInvalid(adminToken, productId, brokenProduct, "");
    }

    @Test
    void updateProductInvalidPrice() {
        String adminToken = getAdminToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestInvalid(adminToken, productId, objectMapper.createObjectNode().put("price", -1.0), "price");

        updateProductRequestInvalid(adminToken, productId, objectMapper.createObjectNode().put("price", "wonderful..."), "");
    }
}
