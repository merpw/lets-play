package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.AuthFactory.getAccessToken;

class UserSecurityTests extends AbstractControllerTests {
    @Test
    void usersShouldBePrivate() {
        when().get("/users").then().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    void usersShouldBeAccessibleForAdmin() {
        String adminToken = getAdminToken();

        given().auth().oauth2(adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(1));
    }

    @Test
    void usersShouldNotBeAccessibleForBasicUser() {
        var testUser = new AuthFactory.TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        var req = given().auth().oauth2(token).when();

        req.get("/users")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.get("/users/12345")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.post("/users/add")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.put("/users/12345")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.delete("/users/12345")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void usersGetId() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();
        testUser.register();

        String firstUserId = given().auth().oauth2(adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().jsonPath().getString("[1].id");

        given().auth().oauth2(adminToken)
                .when()
                .get("/users/" + firstUserId)
                .then()
                .statusCode(HTTP_OK)
                .body("email", is(testUser.email));
    }
}
