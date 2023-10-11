package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.RequestHelpers.authRequest;

class UserDeleteTests extends AbstractControllerTests {
    @Test
    void usersDelete() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();
        testUser.register();

        String firstUserId = authRequest(adminToken).get("/users")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().jsonPath().getString("[1].id");

        authRequest(adminToken).delete("/users/" + firstUserId)
                .then()
                .statusCode(HTTP_OK);


        authRequest(adminToken).get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(1));
    }

    @Test
    void notExists() {
        String adminToken = getAdminToken();

        authRequest(adminToken).delete("/users/12345")
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }

    @Test
    void deleteSelf() {
        String adminToken = getAdminToken();

        var adminId = authRequest(adminToken)
                .get("/auth/profile").then()
                .statusCode(HTTP_OK)
                .extract().body().jsonPath().getString("id");

        // Not sure about this. Maybe restrict self-deletion?

        authRequest(adminToken).delete("/users/" + adminId).then()
                .statusCode(HTTP_OK);

        authRequest(adminToken).get("/users/" + adminId).then()
                .statusCode(HTTP_NOT_FOUND);
    }
}
