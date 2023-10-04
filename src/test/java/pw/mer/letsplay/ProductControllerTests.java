package pw.mer.letsplay;

import org.junit.jupiter.api.Test;

import static java.net.HttpURLConnection.*;
import static pw.mer.letsplay.AuthControllerTests.TestUser;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static pw.mer.letsplay.AuthControllerTests.getAccessToken;

public class ProductControllerTests extends AbstractControllerTests {
    @Test
    public void productsShouldBePublic() {
        when().get("/products").then().statusCode(HTTP_OK);
    }

    @Test
    public void writeOperationsShouldNotBeAvailableForBasicUser() {
        var testUser = new TestUser();
        testUser.register();
        String token = getAccessToken(testUser.email, testUser.password);

        var req = given().auth().oauth2(token).when().contentType("application/json");

        req.post("/products/add")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.put("/products/12345")
                .then()
                .statusCode(HTTP_FORBIDDEN);

        req.delete("/products/12345")
                .then()
                .statusCode(HTTP_FORBIDDEN);
    }

    @Test
    public void addProduct() {
        String adminToken = getAdminToken();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        String postId = given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test name",
                            "description": "Test description",
                            "price": 100.0
                        }
                        """)
                .post("/products/add")
                .then()
                .statusCode(HTTP_OK).extract().body().asString();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(1));

        when().get("/products/" + postId)
                .then()
                .statusCode(HTTP_OK)
                .body("name", is("Test name"))
                .body("description", is("Test description"))
                .body("price", is(100.0f));
    }

    @Test
    public void updateWholeProduct() {
        String adminToken = getAdminToken();

        String postId = given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test product",
                            "description": "Test description",
                            "price": 1.0
                        }
                        """)
                .post("/products/add")
                .then()
                .statusCode(HTTP_OK).extract().body().asString();

        given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Wonderful product",
                            "description": "Wonderful description",
                            "price": 100.0
                        }
                        """)
                .put("/products/" + postId)
                .then()
                .statusCode(HTTP_OK);

        when().get("/products/" + postId)
                .then()
                .statusCode(HTTP_OK)
                .body("name", is("Wonderful product"))
                .body("description", is("Wonderful description"))
                .body("price", is(100.0f));
    }

    @Test
    public void updateProductName() {
        String adminToken = getAdminToken();

        String postId = given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Wondrful product",
                            "description": "Test description",
                            "price": 100.0
                        }
                        """)
                .post("/products/add")
                .then()
                .statusCode(HTTP_OK).extract().body().asString();

        given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Wonderful product"
                        }
                        """)
                .put("/products/" + postId)
                .then()
                .statusCode(HTTP_OK);

        when().get("/products/" + postId)
                .then()
                .statusCode(HTTP_OK)
                .body("name", is("Wonderful product"));
    }

    @Test
    public void deleteProduct() {
        String adminToken = getAdminToken();

        String postId = given().auth().oauth2(adminToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Test product",
                            "description": "Test description",
                            "price": 1.0
                        }
                        """)
                .post("/products/add")
                .then()
                .statusCode(HTTP_OK).extract().body().asString();

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(1));

        given().auth().oauth2(adminToken)
                .delete("/products/" + postId)
                .then()
                .statusCode(HTTP_OK);

        when().get("/products").then().statusCode(HTTP_OK).body("$.size()", is(0));

        given().auth().oauth2(adminToken)
                .delete("/products/" + postId)
                .then()
                .statusCode(HTTP_NOT_FOUND);
    }
}
