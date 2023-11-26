package pw.mer.letsplay.media;

import org.junit.jupiter.api.Test;

import pw.mer.letsplay.AbstractControllerTests;

class MediaUploadTests extends AbstractControllerTests {
    @Test
    void uploadMedia() {
        var adminToken = getAdminToken();

        var mediaData = TestMediaFactory.generateRandomData(1024);

        var testMedia = new TestMediaFactory.TestMedia(mediaData);
        var mediaId = testMedia.upload(adminToken);

        testMedia.requestCheck(mediaId);
    }
}
