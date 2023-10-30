package pw.mer.shared;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestHelpers {
    public static RequestSpecification authRequest(String token) {
        return RestAssured.given().auth().oauth2(token);
    }

    public static RequestSpecification jsonBodyRequest(String token, Object body) {
        return authRequest(token).contentType("application/json").body(body);
    }
}
