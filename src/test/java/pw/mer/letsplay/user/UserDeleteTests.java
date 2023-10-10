package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;

class UserDeleteTests extends AbstractControllerTests {
    @Test
    void usersDelete() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();
        testUser.register();

        var req = given().auth().oauth2(adminToken).when();

        String firstUserId = req.get("/users")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().jsonPath().getString("[1].id");


        req.delete("/users/" + firstUserId)
                .then()
                .statusCode(HTTP_OK);


        req.get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(1));
    }
}
