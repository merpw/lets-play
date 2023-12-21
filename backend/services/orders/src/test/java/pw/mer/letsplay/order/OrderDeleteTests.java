package pw.mer.letsplay.order;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.shared.SharedAuthFactory;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.shared.RequestHelpers.authRequest;

class OrderDeleteTests extends AbstractControllerTests {
    @Test
    void adminCanDeleteOrder() {
        var seller = getSeller();
        var buyer = new SharedAuthFactory.TestUser();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        var buyerToken = getAccessToken(buyer);
        var orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        var adminToken = getAdminToken();
        authRequest(adminToken).delete("/orders/" + orderId).then().statusCode(HTTP_OK);
    }

    @Test
    void nonAdminCanNotDeleteOrder() {
        var seller = getSeller();
        var buyer = new SharedAuthFactory.TestUser();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        var buyerToken = getAccessToken(buyer);
        var orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        var sellerToken = getAccessToken(seller);
        authRequest(sellerToken).delete("/orders/" + orderId).then().statusCode(HTTP_FORBIDDEN);
    }
}
