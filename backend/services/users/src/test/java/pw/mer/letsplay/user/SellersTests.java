package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;

public class SellersTests extends AbstractControllerTests {
    @Test
    public void sellersShouldBePublic() {
        var testSeller = new AuthFactory.TestUser("seller");
        var sellerId = testSeller.register().statusCode(HTTP_OK).extract().asString();

        when().get("/users/" + sellerId).then().statusCode(HTTP_OK)
                .body("name", is(testSeller.name));
    }
}
