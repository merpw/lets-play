package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.shared.RequestHelpers.authRequest;

class UserSecurityTests extends AbstractControllerTests {
    @Test
    void usersShouldBePrivate() {
        when().get("/users").then().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    void usersListPermissions() {
        String adminToken = getAdminToken();

        authRequest(adminToken).get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(1));

        when().get("/users").then().statusCode(HTTP_UNAUTHORIZED);

        var testUser = new AuthFactory.TestUser();
        testUser.register();
        var testUserToken = getAccessToken(testUser.email, testUser.password);

        authRequest(testUserToken).get("/users")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void onlyAdminCanEditUsers() {
        var testUser = new AuthFactory.TestUser();
        testUser.register();

        String token = getAccessToken(testUser.email, testUser.password);

        var req = given().auth().oauth2(token).when();

        req.post("/users/add")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        var basicUser = new AuthFactory.TestUser();
        var basicUserId = basicUser.register().statusCode(HTTP_OK).extract().asString();

        req.put("/users/" + basicUserId)
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.delete("/users/" + basicUserId)
                .then()
                .statusCode(HTTP_FORBIDDEN);

    }

    @Test
    void basicUsersShouldBePrivate() {
        var testUser = new AuthFactory.TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        var basicUser = new AuthFactory.TestUser();
        var basicUserId = basicUser.register().statusCode(HTTP_OK).extract().asString();

        authRequest(token).get("/users/" + basicUserId)
                .then()
                .statusCode(HTTP_NOT_FOUND);
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
