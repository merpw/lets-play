package pw.mer.letsplay.media;

import org.junit.jupiter.api.Test;

import pw.mer.letsplay.AbstractControllerTests;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static pw.mer.shared.RequestHelpers.authRequest;

class MediaUploadTests extends AbstractControllerTests {
    @Test
    void validPNG() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData, "image/png");
        var mediaId = testMedia.upload(adminToken);

        testMedia.requestCheck(mediaId);
    }

    @Test
    void validJPEG() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData, "image/jpeg");
        var mediaId = testMedia.upload(adminToken);

        testMedia.requestCheck(mediaId);
    }

    @Test
    void validWebP() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData, "image/webp");
        var mediaId = testMedia.upload(adminToken);

        testMedia.requestCheck(mediaId);
    }

    @Test
    void wrongContentType() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        authRequest(adminToken)
                .multiPart("file", "file",
                        mediaData, "totally/wrong")
                .post("/media/upload")
                .then().statusCode(SC_BAD_REQUEST);

        authRequest(adminToken)
                .multiPart("file", "file",
                        mediaData, "text/plain")
                .post("/media/upload")
                .then().statusCode(SC_BAD_REQUEST);
    }
}
