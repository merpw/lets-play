package pw.mer.letsplay.product;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.letsplay.AuthFactory.TestUser;
import static pw.mer.letsplay.AuthFactory.getAccessToken;

class ProductSecurityTests extends AbstractControllerTests {
    @Test
    void productsShouldBePublic() {
        when().get("/products").then().statusCode(HTTP_OK);
    }

    @Test
    void writeOperationsShouldNotBeAvailableForBasicUser() {
        var testUser = new TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        RequestSpecification req = given().auth().oauth2(token)
                .when().contentType("application/json");

        req.post("/products/add").then().statusCode(HTTP_FORBIDDEN);

        req.put("/products/12345").then().statusCode(HTTP_FORBIDDEN);

        req.delete("/products/12345").then().statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void notOwnerOfTheProduct() {
        var adminToken = getAdminToken();

        var secondAdmin = new TestUser();
        given().auth().oauth2(adminToken).contentType("application/json")
                .when()
                .body(secondAdmin.getObjectNode().put("role", "admin"))
                .post("/users/add")
                .then()
                .statusCode(HTTP_OK);

        var secondAdminToken = getAccessToken(secondAdmin.email, secondAdmin.password);

        var testProduct = new TestProductFactory.TestProduct();
        var productId = testProduct.requestAdd(adminToken).statusCode(HTTP_OK)
                .extract().body().asString();

        var req = given().auth().oauth2(secondAdminToken).contentType("application/json")
                .then().statusCode(HTTP_FORBIDDEN).request();

        req.body(testProduct).put("/products/" + productId);

        req.delete("/products/" + productId);
    }
}
