package pw.mer.letsplay;

import org.junit.jupiter.api.Test;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

class CustomErrorTests extends AbstractControllerTests {
    @Test
    void endpointDoesNotExist() {
        String sellerToken = getAccessToken(getSeller());

        authRequest(sellerToken).get("/no/such/endpoint")
                .then().statusCode(HTTP_NOT_FOUND);
    }

    @Test
    void errorsShouldFollowRFC7807() {
        String sellerToken = getAccessToken(getSeller());

        authRequest(sellerToken).get("/no/such/endpoint")
                .then().statusCode(HTTP_NOT_FOUND)
                .body("title", is(not(emptyOrNullString())),
                        "status", is(HTTP_NOT_FOUND));

        jsonBodyRequest(sellerToken, "{}").post("/orders/add")
                .then().statusCode(HTTP_BAD_REQUEST)
                .body("title", is(not(emptyOrNullString())),
                        "status", is(HTTP_BAD_REQUEST),
                        "detail", is(not(emptyOrNullString()))
                );
    }

    @Test
    void firewall() {
        String sellerToken = getAccessToken(getSeller());

        authRequest(sellerToken).get("//////this/is/not/allowed///")
                .then().statusCode(HTTP_BAD_REQUEST);

        authRequest(sellerToken).get("/orders;with=semicolon")
                .then().statusCode(HTTP_BAD_REQUEST);

        authRequest(sellerToken).request("TRACE", "/orders")
                .then().statusCode(HTTP_BAD_REQUEST);
    }
}
