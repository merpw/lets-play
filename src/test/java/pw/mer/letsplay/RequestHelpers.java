package pw.mer.letsplay;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class RequestHelpers {
    public static RequestSpecification authRequest(String token) {
        return RestAssured.given().auth().oauth2(token);
    }

    public static RequestSpecification jsonBodyRequest(String token, Object body) {
        return authRequest(token).contentType("application/json").body(body);
    }
}
