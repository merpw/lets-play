package pw.mer.letsplay;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

public class AuthControllerTests extends AbstractControllerTests {
    public static String getAccessToken(String email, String password) {
        return given()
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """
                        .formatted(email, password))
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().asString();
    }

    public static class TestUser {
        public String name;
        public String email;
        public String password;

        public ValidatableResponse register() {
            return given()
                    .when()
                    .contentType("application/json")
                    .body("""
                            {
                                "name": "%s",
                                "email": "%s",
                                "password": "%s"
                            }
                            """
                            .formatted(name, email, password))
                    .and()
                    .post("/auth/register").then();
        }

        public TestUser() {
            String random = RandomStringUtils.randomAlphanumeric(5);
            this.name = "testUser" + random;
            this.email = "testUser" + random + "@mer.pw";
            this.password = "testUserPassword" + random;
        }
    }

    @Test
    void login() {
        var testUser = new TestUser();
        testUser.register().statusCode(HTTP_OK);

        getAccessToken(testUser.email, testUser.password);
    }

    @Test
    void registerValid() {
        var testUser = new TestUser();
        testUser.register().statusCode(HTTP_OK);
    }

    @Test
    void registerEmailExists() {
        var firstTestUser = new TestUser();
        firstTestUser.register().statusCode(HTTP_OK);

        var secondTestUser = new TestUser();
        secondTestUser.email = firstTestUser.email;

        secondTestUser.register().statusCode(HTTP_BAD_REQUEST);
    }
}
