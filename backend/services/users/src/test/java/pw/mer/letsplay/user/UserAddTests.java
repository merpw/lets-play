package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

class UserAddTests extends AbstractControllerTests {
    @Test
    void usersAddBasicUser() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");

        var userId = testUser.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        testUser.requestCheck(adminToken, userId);
    }

    @Test
    void usersAddAdmin() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("admin");

        var userId = testUser.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().asString();

        testUser.requestCheck(adminToken, userId);
    }

    @Test
    void usersAddSeller() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("seller");
        testUser.role = "seller";

        var userId = testUser.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().asString();

        testUser.requestCheck(adminToken, userId);
    }

    @Test
    void usersAddEmailTaken() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.requestAdd(adminToken).statusCode(HTTP_OK);

        var testUser2 = new TestUserFactory.TestUser("user");
        testUser2.email = testUser.email;

        testUser2.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void usersAddInvalidEmail() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.email = "invalidEmail";

        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void usersAddInvalidRole() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.role = "invalidRole";

        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void usersAddInvalidName() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.name = null;

        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.name = "";
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.name = "a";
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.name = "a".repeat(100);
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void usersAddInvalidPassword() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.password = null;

        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.password = "";
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.password = "a";
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);

        testUser.password = "a".repeat(100);
        testUser.requestAdd(adminToken).statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void usersAddValidImage() {
        String adminToken = getAdminToken();

        var testUser = new TestUserFactory.TestUser("user");
        testUser.image = new UUID(0, 0).toString();

        var userId = testUser.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        testUser.requestCheck(adminToken, userId);
    }
}
