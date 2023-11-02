package pw.mer.shared;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class SharedAbstractControllerTests {
    @Container
    static MongoDBContainer mongoDB;

    static {
        mongoDB = new MongoDBContainer("mongo:latest");
        mongoDB.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", mongoDB::getFirstMappedPort);
    }

    public void clean() {
    }

    public void init() {
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    @SuppressWarnings("java:S2696")
        // setting RestAssured.port is fine as we execute tests sequentially
    void beforeEach() {
        RestAssured.port = port;

        clean();
        init();
    }
}
