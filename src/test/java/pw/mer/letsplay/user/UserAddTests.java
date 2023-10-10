package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

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
}
