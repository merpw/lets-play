package pw.mer.letsplay.product;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.*;
import static pw.mer.letsplay.AuthFactory.TestUser;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static pw.mer.letsplay.AuthFactory.getAccessToken;

class ProductSecurityTests extends AbstractControllerTests {
    @Test
    void productsShouldBePublic() {
        when().get("/products").then().statusCode(HTTP_OK);
    }

    @Test
    void writeOperationsShouldNotBeAvailableForBasicUser() {
        var testUser = new TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        RequestSpecification req = given().auth().oauth2(token)
                .when().contentType("application/json");

        req.post("/products/add").then().statusCode(HTTP_FORBIDDEN);

        req.put("/products/12345").then().statusCode(HTTP_FORBIDDEN);

        req.delete("/products/12345").then().statusCode(HTTP_FORBIDDEN);
    }
}
