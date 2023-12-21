package pw.mer.letsplay.order;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.shared.SharedAuthFactory;

import static java.net.HttpURLConnection.*;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

class OrderUpdateTests extends AbstractControllerTests {

    @Test
    void updateOrderStatusValid() {
        var seller = getSeller();
        var buyer = new SharedAuthFactory.TestUser();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        String sellerToken = getAccessToken(seller);
        String orderId = testOrder.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        jsonBodyRequest(sellerToken, "{\"status\": \"completed\"}")
                .put("/orders/" + orderId)
                .then().statusCode(HTTP_OK);

        jsonBodyRequest(sellerToken, "{\"status\": \"canceled\"}")
                .put("/orders/" + orderId)
                .then().statusCode(HTTP_OK);
    }

    @Test
    void invalidStatus() {
        var seller = getSeller();
        var buyer = new SharedAuthFactory.TestUser();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        String sellerToken = getAccessToken(seller);
        String orderId = testOrder.requestAdd(sellerToken).statusCode(HTTP_OK).extract().asString();

        jsonBodyRequest(sellerToken, "{\"status\": \"invalid\"}")
                .put("/orders/" + orderId)
                .then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void buyerCanNotChangeStatus() {
        var seller = getSeller();
        var buyer = new SharedAuthFactory.TestUser();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        String buyerToken = getAccessToken(buyer);
        String orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        jsonBodyRequest(buyerToken, "{\"status\": \"completed\"}")
                .put("/orders/" + orderId)
                .then().statusCode(HTTP_FORBIDDEN);

        jsonBodyRequest(buyerToken, "{\"status\": \"cancelled\"}")
                .put("/orders/" + orderId)
                .then().statusCode(HTTP_FORBIDDEN);
    }
}
