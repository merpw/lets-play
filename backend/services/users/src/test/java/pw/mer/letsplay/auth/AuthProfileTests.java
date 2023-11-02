package pw.mer.letsplay.auth;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.with;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;
import static pw.mer.shared.RequestHelpers.authRequest;


class AuthProfileTests extends AbstractControllerTests {
    @Test
    void profileAdmin() {
        String adminToken = getAdminToken();

        authRequest(adminToken).get("/auth/profile")
                .then().statusCode(HTTP_OK)
                .body("name", is(env.getProperty("initial.admin.name")));
    }

    @Test
    void shouldNotSendPassword() {
        String adminToken = getAdminToken();

        authRequest(adminToken).get("/auth/profile")
                .then().statusCode(HTTP_OK)
                .body("$", not(hasKey("password")));
    }

    @Test
    void deletedUser() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();
        testUser.register();

        String userToken = AuthFactory.getAccessToken(testUser.email, testUser.password);

        String userId = authRequest(userToken).get("/auth/profile")
                .then().statusCode(HTTP_OK).extract().path("id");

        authRequest(adminToken).delete("/users/" + userId).then().statusCode(HTTP_OK);

        authRequest(userToken).get("/auth/profile").then().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    void unauthorized() {
        with().get("/auth/profile").then().statusCode(HTTP_UNAUTHORIZED);
    }
}
