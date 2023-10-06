package pw.mer.letsplay.auth;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.letsplay.AuthFactory;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

class AuthControllerTests extends AbstractControllerTests {
    @Test
    void login() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);

        AuthFactory.getAccessToken(testUser.email, testUser.password);
    }

    @Test
    void registerValid() {
        var testUser = new AuthFactory.TestUser();
        testUser.register().statusCode(HTTP_OK);
    }

    @Test
    void registerEmailExists() {
        var firstTestUser = new AuthFactory.TestUser();
        firstTestUser.register().statusCode(HTTP_OK);

        var secondTestUser = new AuthFactory.TestUser();
        secondTestUser.email = firstTestUser.email;

        secondTestUser.register().statusCode(HTTP_BAD_REQUEST);
    }
}
