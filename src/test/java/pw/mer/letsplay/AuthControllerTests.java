package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.letsplay.auth.WebSecurityConfig.passwordEncoder;

public class AuthControllerTests extends AbstractControllerTests {

    public static String getAccessToken(String email, String password) {
        return given()
                .when()
                .contentType("application/json")
                .body(format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password))
                .and()
                .post("/auth/login")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().asString();
    }

    @Test
    void login() {
        var password = "test";
        var user = new User("test", "test@mer.pw", passwordEncoder().encode(password), ERole.USER);
        userRepo.save(user);

        getAccessToken(user.getEmail(), password);
    }
}
