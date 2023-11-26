package pw.mer.letsplay.media;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.MimeType;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

import static io.restassured.RestAssured.when;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertArrayEquals;
import static pw.mer.shared.RequestHelpers.authRequest;


public class TestMediaFactory {
    @AllArgsConstructor
    @Getter
    static class TestMedia {
        private final byte[] data;
        private final String contentType;

        public String upload(String adminToken) {
            return authRequest(adminToken)
                    .multiPart("file", "file", data, contentType)
                    .post("/media/upload")
                    .then().statusCode(SC_CREATED)
                    .extract().body().asString();
        }

        /**
         * Checks if media with given id is equal to this test media
         */
        public void requestCheck(String mediaId) {
            var body = when()
                    .get("/media/" + mediaId)
                    .then().statusCode(SC_OK)
                    .extract().body().asByteArray();
            assertArrayEquals(data, body);
        }
    }

    static byte[] generateRandomData(int size) {
        return RandomUtils.nextBytes(size);
    }
}
