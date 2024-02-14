package pw.mer.letsplay.order;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.SharedAuthFactory.TestUser;

class OrderSecurityTests extends AbstractControllerTests {
    @Test
    void canGetOwnOrders() {
        var buyer = new TestUser();
        var seller = getSeller();

        var buyerToken = getAccessToken(buyer);
        var sellerToken = getAccessToken(seller);

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());
        var orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        testOrder.requestCheck(buyerToken, orderId);
        testOrder.requestCheck(sellerToken, orderId);

        authRequest(buyerToken).get("/orders").then().statusCode(HTTP_OK).body("$.size()", is(1));
        authRequest(sellerToken).get("/orders").then().statusCode(HTTP_OK).body("$.size()", is(1));
    }

    @Test
    void canNotGetOthersOrders() {
        var buyer = new TestUser();
        var seller = getSeller();

        var buyerToken = getAccessToken(buyer);

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());
        var orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        var unknownBuyer = new TestUser();
        var unknownBuyerToken = getAccessToken(unknownBuyer);

        authRequest(unknownBuyerToken).get("/orders/" + orderId).then().statusCode(HTTP_FORBIDDEN);
        authRequest(unknownBuyerToken).get("/orders").then().statusCode(HTTP_OK).body("$.size()", is(0));

        var unknownSeller = new TestUser(List.of("orders:read"));
        var unknownSellerToken = getAccessToken(unknownSeller);

        authRequest(unknownSellerToken).get("/orders/" + orderId).then().statusCode(HTTP_FORBIDDEN);
        authRequest(unknownSellerToken).get("/orders").then().statusCode(HTTP_OK).body("$.size()", is(0));
    }

    @Test
    void adminCanGetEverything() {
        var adminToken = getAdminToken();

        var buyer = new TestUser();
        var seller = getSeller();

        var testOrder = new TestOrderFactory.TestOrder(buyer.getId(), seller.getId());

        var buyerToken = getAccessToken(buyer);
        var orderId = testOrder.requestAdd(buyerToken).statusCode(HTTP_OK).extract().asString();

        authRequest(adminToken).get("/orders/" + orderId).then().statusCode(HTTP_OK);
        authRequest(adminToken).get("/orders").then().statusCode(HTTP_OK).body("$.size()", is(1));
    }
}
