package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.AuthControllerTests.getAccessToken;
import static pw.mer.letsplay.auth.WebSecurityConfig.passwordEncoder;

public class UserControllerTests extends AbstractControllerTests {
    @Test
    void usersShouldBePrivate() {
        when().get("/users").then().statusCode(HTTP_UNAUTHORIZED);
    }

    @Test
    void usersShouldBeAccessibleForAdmin() {
        var password = "test";
        var user = new User("admin", "admin@example.com", passwordEncoder().encode(password), ERole.ADMIN);
        userRepo.save(user);

        String token = getAccessToken(user.getEmail(), password);

        given().auth().oauth2(token)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_OK)
                .body("[0].id", is(user.getId()));
    }

    @Test
    void usersShouldNotBeAccessibleForBasicUser() {
        var password = "test";
        var user = new User("test", "test@example.com", passwordEncoder().encode(password), ERole.USER);
        userRepo.save(user);

        String token = getAccessToken(user.getEmail(), password);

        given().auth().oauth2(token)
                .when()
                .get("/users")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }
}
