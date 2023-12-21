package pw.mer.letsplay.order;

import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

public class TestOrderFactory {
    @AllArgsConstructor
    @Getter
    public static class TestOrder {
        @JsonIgnore
        String buyer;

        String seller;
        Double totalPrice;

        List<String> products;

        public ValidatableResponse requestAdd(String token) {
            return jsonBodyRequest(token, this).post("/orders/add").then();
        }

        /**
         * Checks if `GET /orders/{orderId}` returns this order
         *
         * @param token   - token of user who has access to this order
         * @param orderId - id of order to check
         */
        public void requestCheck(String token, String orderId) {
            ValidatableResponse response = authRequest(token)
                    .get("/orders/" + orderId)
                    .then().statusCode(HTTP_OK)
                    .body("buyer", is(buyer),
                            "seller", is(seller),
                            "totalPrice", is(totalPrice.floatValue())
                    );

            if (products != null) {
                response.body("products", is(products));
            }
        }

        public TestOrder(String buyer, String seller) {
            this.buyer = buyer;
            this.seller = seller;

            this.totalPrice = Math.random() * 100;

            this.products = List.of("Test product" + System.currentTimeMillis());
        }
    }
}
