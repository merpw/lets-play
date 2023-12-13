package pw.mer.letsplay.media;

import org.junit.jupiter.api.Test;
import pw.mer.letsplay.AbstractControllerTests;
import pw.mer.shared.SharedAuthFactory;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static pw.mer.shared.RequestHelpers.authRequest;

class MediaSecurityTests extends AbstractControllerTests {
    @Test
    void getShouldBePublic() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData, "image/png");
        var mediaId = testMedia.upload(adminToken);

        testMedia.requestCheck(mediaId);
    }

    @Test
    void unauthorizedCanUpload() {
        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData, "image/png");

        var mediaId = given()
                .multiPart("file", "file",
                        mediaData, "image/png")
                .post("/media/upload")
                .then().statusCode(SC_CREATED)
                .extract().body().asString();

        testMedia.requestCheck(mediaId);
    }

    @Test
    void nonAdminCanUpload() {
        var mediaData = TestMediaFactory.generateRandomData(1024);

        var token = getAccessToken(new SharedAuthFactory.TestUser());

        authRequest(token)
                .multiPart("file", "file",
                        mediaData, "image/png")
                .post("/media/upload")
                .then().statusCode(SC_CREATED);
    }
}
