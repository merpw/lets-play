package pw.mer.letsplay;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import static io.restassured.RestAssured.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LetsPlayApplicationTests {
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:latest");

    @BeforeAll
    static void beforeAll() {
        mongoDB.start();
    }

    @AfterAll
    static void afterAll() {
        mongoDB.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", mongoDB::getFirstMappedPort);
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
    }

    @Test
    public void productsShouldBePublic() {
        when().get("/products").then().statusCode(200);
    }
}
