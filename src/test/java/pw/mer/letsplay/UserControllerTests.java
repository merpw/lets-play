package pw.mer.letsplay;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.model.ERole;
import pw.mer.letsplay.model.User;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

public class UserControllerTests extends AbstractControllerTests {
    @Test
    void usersShouldBePrivate() {
        when().get("/users").then().statusCode(401);
    }

    @Test
    void usersShouldBeAccessibleForAdmin() {
        var password = "test";
        var user = new User("test", "test@example.com", "{noop}" + password, ERole.ADMIN);
        userRepo.save(user);

        given().auth().basic(user.getEmail(), password)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("[0].id", is(user.getId()));
    }
}
