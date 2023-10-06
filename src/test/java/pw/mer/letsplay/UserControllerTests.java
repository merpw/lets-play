package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.model.ERole;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.AuthFactory.getAccessToken;

class UserControllerTests extends AbstractControllerTests {
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

    @Test
    void usersAddBasicUser() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();

        given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s",
                            "role": "%s"
                        }
                        """
                        .formatted(
                                testUser.name,
                                testUser.email,
                                testUser.password,
                                ERole.USER
                        )
                )
                .and()
                .post("/users/add")
                .then()
                .statusCode(HTTP_OK);

        given().auth().oauth2(adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(2))
                .body("[1].email", is(testUser.email))
                .body("[1].role", is("user"));
    }

    @Test
    void usersAddAdmin() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();

        given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s",
                            "role": "%s"
                        }
                        """
                        .formatted(
                                testUser.name,
                                testUser.email,
                                testUser.password,
                                ERole.ADMIN
                        )
                )
                .and()
                .post("/users/add")
                .then()
                .statusCode(HTTP_OK);

        given().auth().oauth2(adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(2))
                .body("[1].email", is(testUser.email))
                .body("[1].role", is("admin"));
    }

    @Test
    void updateWholeUser() {
        String adminToken = getAdminToken();

        var testUser = new AuthFactory.TestUser();
        testUser.register();

        var req = given().auth().oauth2(adminToken).when();

        String firstUserId = req
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().jsonPath().getString("[1].id");

        req.contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s",
                            "role": "%s"
                        }
                        """
                        .formatted(
                                testUser.name,
                                testUser.email,
                                testUser.password,
                                ERole.ADMIN
                        )
                )
                .and()
                .put("/users/" + firstUserId)
                .then()
                .statusCode(HTTP_OK);

        req.get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(2))
                .body("[1].email", is(testUser.email))
                .body("[1].role", is("admin"));
    }

    @Test
    void updatePassword() {
        String adminToken = getAdminToken();

        String userId = given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test name",
                            "email": "test@email.com",
                            "password": "testPassword",
                            "role": "user"
                        }
                        """
                )
                .and()
                .post("/users/add")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().asString();

        given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "password": "newPassword"
                        }
                        """
                )
                .and()
                .put("/users/" + userId)
                .then()
                .statusCode(HTTP_OK);

        getAccessToken("test@email.com", "newPassword");
    }

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
