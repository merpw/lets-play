package pw.mer.letsplay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ValidatableResponse;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;

public class AuthFactory {

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
                    .body(this)
                    .and()
                    .post("/auth/register").then();
        }

        public ObjectNode getObjectNode() {
            return new ObjectMapper().createObjectNode()
                    .put("name", name)
                    .put("email", email)
                    .put("password", password);
        }

        public TestUser() {
            String random = RandomStringUtils.randomAlphanumeric(5);
            this.name = "testUser" + random;
            this.email = "testUser" + random + "@mer.pw";
            this.password = "testUserPassword" + random;
        }
    }
}
