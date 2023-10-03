package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.letsplay.auth.WebSecurityConfig.passwordEncoder;

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

    @Test
    void login() {
        var password = "test";
        var user = new User("test", "test@mer.pw", passwordEncoder().encode(password), ERole.USER);
        userRepo.save(user);

        getAccessToken(user.getEmail(), password);
    }

    @Test
    void registerValid() {
        var name = "test";
        var email = "test@mer.pw";
        var password = "test";

        given()
                .when()
                .contentType("application/json")
                .body("""
                          {
                              "name": "%s",
                              "email": "%s",
                              "password": "%s"
                          }
                        """.formatted(name, email, password))
                .and()
                .post("/auth/register")
                .then()
                .statusCode(HTTP_OK);

        var user = userRepo.findByEmail(email).stream().findFirst().orElseThrow();
        assert user.getName().equals(name);
        assert user.getEmail().equals(email);
    }

    @Test
    void registerEmailExists() {
        var user = new User("test", "test@mer.pw", "test", ERole.USER);
        userRepo.save(user);

        given()
                .when()
                .contentType("application/json")
                .body("""
                          {
                              "name": "test",
                              "email": "%s",
                              "password": "test"
                          }
                        """.formatted(user.getEmail()))
                .and()
                .post("/auth/register")
                .then()
                .statusCode(HTTP_BAD_REQUEST);
    }
}
