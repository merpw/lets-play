package pw.mer.letsplay.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.ValidatableResponse;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.is;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

public class TestUserFactory {

    public static class TestUser {
        String name;
        String email;
        String password;

        String role;

        public ValidatableResponse requestAdd(String token) {
            return jsonBodyRequest(token, this.getObjectNode()).post("/users/add").then();
        }

        /**
         * Checks if `GET /users/{userId}` returns this user
         *
         * @param token  - token of user who has access to this user
         * @param userId - id of user to check
         */
        public void requestCheck(String token, String userId) {

            authRequest(token).get("/users/" + userId)
                    .then().statusCode(HTTP_OK)
                    .body("name", is(name),
                            "email", is(email),
                            "role", is(role));
        }

        public ObjectNode getObjectNode() {
            return new ObjectMapper().createObjectNode()
                    .put("name", name)
                    .put("email", email)
                    .put("password", password)
                    .put("role", role);
        }

        public TestUser(String role) {
            String random = RandomStringUtils.randomAlphanumeric(5);
            this.name = "testUser" + random;
            this.email = "testUser" + random + "@mer.pw";
            this.password = "testUserPassword" + random;
            this.role = role;
        }
    }
}
