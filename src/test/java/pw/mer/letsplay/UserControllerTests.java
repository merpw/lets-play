package pw.mer.letsplay;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.AuthControllerTests.TestUser;
import static pw.mer.letsplay.AuthControllerTests.getAccessToken;

public class UserControllerTests extends AbstractControllerTests {
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
        var testUser = new TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        given().auth().oauth2(token)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void usersAddAdmin() {
        String adminToken = getAdminToken();

        given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "name": "testUser",
                            "email": "testUser@mer.pw",
                            "password": "testUserPassword"
                        }
                        """
                )
                .and()
                .post("/users/add-admin")
                .then()
                .statusCode(HTTP_OK);

        given().auth().oauth2(adminToken)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("$.size()", is(2))
                .body("[1].email", is("testUser@mer.pw"));
    }
}
