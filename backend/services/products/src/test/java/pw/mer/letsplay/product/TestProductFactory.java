package pw.mer.letsplay.product;

import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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

        Number quantity;

        List<String> images;

        public ValidatableResponse requestAdd(String token) {
            return jsonBodyRequest(token, this).post("/products/add").then();
        }

        /**
         * Checks if `GET /products/{productId}` returns this product
         *
         * @param token     - token of user who has access to this product
         * @param productId - id of product to check
         */
        public void requestCheck(String token, String productId) {
            ValidatableResponse response = authRequest(token)
                    .get("/products/" + productId)
                    .then().statusCode(HTTP_OK)
                    .body("name", is(name),
                            "description", is(description),
                            "price", is(price.floatValue()),
                            "quantity", is(quantity)
                    );

            if (images != null) {
                response.body("images", is(images));
            }
        }

        public TestProduct() {
            this.name = "Test product" + System.currentTimeMillis();
            this.description = "Test description" + System.currentTimeMillis();
            this.price = Math.random() * 100;
            this.quantity = (int) (Math.random() * 100);
        }
    }
}
