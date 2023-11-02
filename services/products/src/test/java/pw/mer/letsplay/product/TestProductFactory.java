package pw.mer.letsplay.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

public class TestProductFactory {
    @AllArgsConstructor
    @Getter
    public static class TestProduct {
        String name;
        String description;
        Double price;

        public ValidatableResponse requestAdd(String token) {
            return jsonBodyRequest(token, this.getObjectNode()).post("/products/add").then();
        }

        /**
         * Checks if `GET /products/{productId}` returns this product
         *
         * @param token     - token of user who has access to this product
         * @param productId - id of product to check
         */
        public void requestCheck(String token, String productId) {
            authRequest(token)
                    .get("/products/" + productId)
                    .then().statusCode(HTTP_OK)
                    .body("name", is(name),
                            "description", is(description),
                            "price", is(price.floatValue()));
        }

        public ObjectNode getObjectNode() {
            return new ObjectMapper().createObjectNode()
                    .put("name", name)
                    .put("description", description)
                    .put("price", price);
        }

        public TestProduct() {
            this.name = "Test product" + System.currentTimeMillis();
            this.description = "Test description" + System.currentTimeMillis();
            this.price = Math.random() * 100;
        }
    }
}
