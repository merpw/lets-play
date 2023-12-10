package pw.mer.letsplay.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.matchesRegex;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

class ProductUpdateTests extends AbstractControllerTests {
    static void updateProductRequestValid(String adminToken, String productId, Object body) {
        jsonBodyRequest(adminToken, body).put("/products/" + productId).then().statusCode(HTTP_OK);
    }

    @Test
    void updateWholeProductValid() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        var newTestProduct = new TestProductFactory.TestProduct();
        updateProductRequestValid(sellerToken, productId, newTestProduct);
    }

    @Test
    void updateProductFieldsValid() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestValid(sellerToken, productId,
                objectMapper.createObjectNode().put("name", "New name")
        );
        testProduct.name = "New name";

        testProduct.requestCheck(sellerToken, productId);

        updateProductRequestValid(sellerToken, productId,
                objectMapper.createObjectNode().put("description", "New description")
        );
        testProduct.description = "New description";

        testProduct.requestCheck(sellerToken, productId);

        updateProductRequestValid(sellerToken, productId,
                objectMapper.createObjectNode().put("price", 100.0)
        );
        testProduct.price = 100.0;

        testProduct.requestCheck(sellerToken, productId);
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
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestInvalid(sellerToken, productId, objectMapper.createObjectNode().put("name", ""), "name");

        updateProductRequestInvalid(sellerToken, productId, objectMapper.createObjectNode().put("name", "a"), "name");

        updateProductRequestInvalid(sellerToken, productId, objectMapper.createObjectNode().put("name", "a".repeat(100)), "name");

        var brokenProduct = objectMapper.createObjectNode();
        brokenProduct.putObject("name").put("wow", "object as a name!");

        updateProductRequestInvalid(sellerToken, productId, brokenProduct, "");
    }

    @Test
    void updateProductInvalidPrice() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        updateProductRequestInvalid(sellerToken, productId, objectMapper.createObjectNode().put("price", -1.0), "price");

        updateProductRequestInvalid(sellerToken, productId, objectMapper.createObjectNode().put("price", "wonderful..."), "");
    }

    @Test
    void updateProductImages() {
        String sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();

        String productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        testProduct.images = List.of(new UUID(0, 0).toString());

        var node = objectMapper.createObjectNode();

        var images = node.putArray("images");

        testProduct.images.forEach(images::add);

        updateProductRequestValid(sellerToken, productId, node);

        testProduct.requestCheck(sellerToken, productId);


    }
}
