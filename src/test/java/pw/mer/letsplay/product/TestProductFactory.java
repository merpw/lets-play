package pw.mer.letsplay.product;

import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;

public class TestProductFactory {
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @Getter
    public static class TestProduct {
        String name;
        String description;
        Double price;

        public ValidatableResponse requestAdd(String token) {
            return given()
                    .auth().oauth2(token)
                    .when()
                    .contentType("application/json")
                    .body(this)
                    .and()
                    .post("/products/add").then();
        }

        /**
         * Checks if `GET /products/{productId}` returns this product
         *
         * @param token     - token of user who has access to this product
         * @param productId - id of product to check
         */
        public void requestCheck(String token, String productId) {
            given().auth().oauth2(token)
                    .when()
                    .get("/products/" + productId)
                    .then().statusCode(HTTP_OK)
                    .body("name", is(name),
                            "description", is(description),
                            "price", is(price.floatValue()));
        }

        public TestProduct() {
            this.name = "Test product" + System.currentTimeMillis();
            this.description = "Test description" + System.currentTimeMillis();
            this.price = Math.random() * 100;
        }

        public static TestProduct Empty() {
            return new TestProduct(null, null, null);
        }
    }
}
