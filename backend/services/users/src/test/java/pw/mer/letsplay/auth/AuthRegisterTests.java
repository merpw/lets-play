package pw.mer.letsplay.auth;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.core.Is.is;
import static pw.mer.shared.RequestHelpers.authRequest;

class AuthRegisterTests extends AbstractControllerTests {
    @Test
    void registerValid() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);
    }

    @Test
    void shouldReturnUserId() {
        var testUser = new AuthFactory.TestUser();
        String userId = testUser.register().statusCode(HTTP_OK).extract().asString();

        var token = AuthFactory.getAccessToken(testUser.email, testUser.password);

        authRequest(token).get("/auth/profile").then().statusCode(HTTP_OK).body("id", is(userId));
    }

    @Test
    void registerEmailExists() {
        var firstTestUser = new AuthFactory.TestUser();
        firstTestUser.register().statusCode(HTTP_OK);

        var secondTestUser = new AuthFactory.TestUser();
        secondTestUser.email = firstTestUser.email;

        secondTestUser.register().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void registerInvalidEmail() {
        var testUser = new AuthFactory.TestUser();

        testUser.email = null;
        testUser.register().statusCode(HTTP_BAD_REQUEST);

        testUser.email = "invalidEmail";
        testUser.register().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void registerInvalidPassword() {
        var testUser = new AuthFactory.TestUser();

        testUser.password = null;
        testUser.register().statusCode(HTTP_BAD_REQUEST);

        testUser.password = "0";
        testUser.register().statusCode(HTTP_BAD_REQUEST);

        testUser.password = "a".repeat(100);
        testUser.register().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void registerInvalidName() {
        var testUser = new AuthFactory.TestUser();

        testUser.name = null;
        testUser.register().statusCode(HTTP_BAD_REQUEST);

        testUser.name = "";
        testUser.register().statusCode(HTTP_BAD_REQUEST);

        testUser.name = "a".repeat(100);
        testUser.register().statusCode(HTTP_BAD_REQUEST);
    }

    @Test
    void registerAsSeller() {
        var testSeller = new AuthFactory.TestUser("seller");

        testSeller.register().statusCode(HTTP_OK);
    }

    @Test
    void invalidRegisterAsAdmin() {
        var testAdmin = new AuthFactory.TestUser("admin");

        testAdmin.register().statusCode(HTTP_BAD_REQUEST);
    }
}
