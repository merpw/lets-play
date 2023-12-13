package pw.mer.letsplay.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import java.util.UUID;

import static java.net.HttpURLConnection.*;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;

class UserUpdateTests extends AbstractControllerTests {
    @Test
    void updateWholeUser() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var newTestUser = new TestUserFactory.TestUser("admin");

        jsonBodyRequest(adminToken, newTestUser.getObjectNode())
                .put("/users/" + testUserId)
                .then().statusCode(HTTP_OK);
    }

    @Test
    void updateEmailTaken() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var testUser2 = new TestUserFactory.TestUser("user");
        testUser2.requestAdd(adminToken).statusCode(HTTP_OK);

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("email", testUser2.email))
                .put("/users/" + testUserId)
                .then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void updateIndividualFields() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("name", "New name"))
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);

        testUser.name = "New name";

        testUser.requestCheck(adminToken, testUserId);


        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("email", "new@email.com"))
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);

        testUser.email = "new@email.com";

        testUser.requestCheck(adminToken, testUserId);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("role", "admin"))
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);

        testUser.role = "admin";

        testUser.requestCheck(adminToken, testUserId);
    }

    @Test
    void updatePassword() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        String newPassword = "newPassword";

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("password", newPassword))
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);

        getAccessToken(testUser.email, newPassword);
    }

    @Test
    void updateNameInvalid() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("name", ""))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("name", "a"))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("name", "a".repeat(100)))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        var brokenRequest = objectMapper.createObjectNode();
        brokenRequest.putObject("name").put("wow", "object as a name!");

        jsonBodyRequest(adminToken, brokenRequest)
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void updateEmailInvalid() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("email", ""))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("email", "invalidEmail"))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void updateRoleInvalid() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("role", "invalidRole"))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void updatePasswordInvalid() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("password", ""))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("password", "a"))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("password", "a".repeat(100)))
                .put("/users/" + testUserId).then().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void updateNonExistingUser() {
        String adminToken = getAdminToken();

        var objectMapper = new ObjectMapper();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("name", "New name"))
                .put("/users/DoesNotExist").then().statusCode(HTTP_NOT_FOUND);
    }

    @Test
    void updateImage() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        String testUserId = testUser.requestAdd(adminToken).statusCode(HTTP_OK).extract().asString();

        var objectMapper = new ObjectMapper();

        String newImage = new UUID(0, 0).toString();

        jsonBodyRequest(adminToken, objectMapper.createObjectNode().put("image", newImage))
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);

        testUser.image = newImage;

        testUser.requestCheck(adminToken, testUserId);
    }

    @Test
    void basicUserCanUpdateSelf() {
        var testUser = new AuthFactory.TestUser();

        String testUserId = testUser.register().statusCode(HTTP_OK).extract().asString();

        String token = getAccessToken(testUser.email, testUser.password);

        jsonBodyRequest(token, "{\"name\": \"new name\"}")
                .put("/users/" + testUserId).then().statusCode(HTTP_OK);
    }
}
