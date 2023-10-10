package pw.mer.letsplay.user;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.letsplay.AuthFactory.getAccessToken;

class UserUpdateTests extends AbstractControllerTests {
    @Test
    void updatePassword() {
        String adminToken = getAdminToken();

        String userId = given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test name",
                            "email": "test@email.com",
                            "password": "testPassword",
                            "role": "user"
                        }
                        """
                )
                .and()
                .post("/users/add")
                .then()
                .statusCode(HTTP_OK)
                .extract().body().asString();

        given().auth().oauth2(adminToken)
                .when()
                .contentType("application/json")
                .body("""
                        {
                            "password": "newPassword"
                        }
                        """
                )
                .and()
                .put("/users/" + userId)
                .then()
                .statusCode(HTTP_OK);

        getAccessToken("test@email.com", "newPassword");
    }

}
