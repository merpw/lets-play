package pw.mer.letsplay.product;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static pw.mer.shared.RequestHelpers.authRequest;
import static pw.mer.shared.RequestHelpers.jsonBodyRequest;
import static pw.mer.shared.SharedAuthFactory.TestUser;

class ProductSecurityTests extends AbstractControllerTests {
    @Test
    void productsShouldBePublic() {
        when().get("/products").then().statusCode(HTTP_OK);
    }

    @Test
    void writeOperationsShouldNotBeAvailableForBasicUser() {
        var testUser = new TestUser();
        String token = getAccessToken(testUser);

        RequestSpecification req = given().auth().oauth2(token)
                .when().contentType("application/json");

        req.post("/products/add").then().statusCode(HTTP_FORBIDDEN);

        req.put("/products/12345").then().statusCode(HTTP_FORBIDDEN);

        req.delete("/products/12345").then().statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void notOwnerOfTheProduct() {
        var sellerToken = getAccessToken(new TestUser(List.of("products:write")));

        var secondSellerToken = getAccessToken(new TestUser(List.of("products:write")));

        var testProduct = new TestProductFactory.TestProduct();
        var productId = testProduct.requestAdd(sellerToken).statusCode(HTTP_OK)
                .extract().asString();

        var req = given().auth().oauth2(secondSellerToken).contentType("application/json")
                .then().statusCode(HTTP_FORBIDDEN).request();

        req.body(testProduct).put("/products/" + productId).then().statusCode(HTTP_FORBIDDEN);

        req.delete("/products/" + productId).then().statusCode(HTTP_FORBIDDEN);
    }

    @Test
    void adminCanChangeAnyProducts() {
        var adminToken = getAdminToken();

        var sellerToken = getSellerToken();

        var testProduct = new TestProductFactory.TestProduct();
        var productId = testProduct.requestAdd(sellerToken)
                .statusCode(HTTP_OK)
                .extract().asString();

        jsonBodyRequest(adminToken, testProduct)
                .put("/products/" + productId)
                .then().statusCode(HTTP_OK);

        authRequest(adminToken).delete("/products/" + productId)
                .then().statusCode(HTTP_OK);
    }
}
