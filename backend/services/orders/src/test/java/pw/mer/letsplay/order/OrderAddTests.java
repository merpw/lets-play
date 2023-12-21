package pw.mer.letsplay.order;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.shared.SharedAuthFactory;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

class OrderAddTests extends AbstractControllerTests {
    @Test
    void orderAddValid() {
        var testUser = new SharedAuthFactory.TestUser();
        String token = getAccessToken(testUser);

        var testSeller = getSeller();

        var order = new TestOrderFactory.TestOrder(testUser.getId(), testSeller.getId());

        String orderId = order.requestAdd(token).statusCode(HTTP_OK).extract().asString();

        order.requestCheck(token, orderId);
    }

    @Test
    void orderAddInvalid() {
        var testUser = new SharedAuthFactory.TestUser();
        String token = getAccessToken(testUser);

        var testSeller = new SharedAuthFactory.TestUser(List.of("orders:write"));

        var order = new TestOrderFactory.TestOrder(testUser.getId(), testSeller.getId());

        var brokenOrder = new TestOrderFactory.TestOrder(testUser.getId(), testSeller.getId());

        brokenOrder.totalPrice = null;
        brokenOrder.requestAdd(token).statusCode(HTTP_BAD_REQUEST);

        brokenOrder.totalPrice = -1.0;
        brokenOrder.requestAdd(token).statusCode(HTTP_BAD_REQUEST);

        brokenOrder = order;

        brokenOrder.products = List.of();
        brokenOrder.requestAdd(token).statusCode(HTTP_BAD_REQUEST);

        brokenOrder.products = null;
        brokenOrder.requestAdd(token).statusCode(HTTP_BAD_REQUEST);
    }
}
