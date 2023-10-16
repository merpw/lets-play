package pw.mer.letsplay.auth;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;

class AuthLoginTests extends AbstractControllerTests {
    @Test
    void loginValid() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);

        AuthFactory.getAccessToken(testUser.email, testUser.password);
    }

    @Test
    void loginNoSuchUser() {
        var testUser = new AuthFactory.TestUser();
        // no registration

        given()
                .when()
                .contentType("application/json")
                .body(testUser.getObjectNode())
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_BAD_REQUEST);

        testUser.email = "invalidEmail";
        given()
                .when()
                .contentType("application/json")
                .body(testUser.getObjectNode())
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void loginInvalidPassword() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);

        given()
                .when()
                .contentType("application/json")
                .body(testUser.getObjectNode().put("password", "invalidPassword"))
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_BAD_REQUEST);

        given()
                .when()
                .contentType("application/json")
                .body(testUser.getObjectNode().remove("password"))
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void loginInvalidBody() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);

        Matcher<Integer> invalidBodyStatusCode = is(oneOf(HTTP_BAD_REQUEST, HTTP_UNSUPPORTED_TYPE));

        given().post("/auth/login").then().statusCode(invalidBodyStatusCode);

        given()
                .when()
                .contentType("application/json")
                .body("")
                .and()
                .post("/auth/login")
                .then()
                .statusCode(invalidBodyStatusCode);

        given()
                .when()
                .contentType("application/json")
                .body("{}")
                .and()
                .post("/auth/login")
                .then()
                .statusCode(invalidBodyStatusCode);
    }
}
