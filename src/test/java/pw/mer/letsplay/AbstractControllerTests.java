package pw.mer.letsplay;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.letsplay.repository.UserRepo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
abstract class AbstractControllerTests {
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

    @LocalServerPort
    private Integer port;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    private CommandLineRunner commandLineRunner;

    @BeforeEach
    void beforeEach() throws Exception {
        RestAssured.port = port;

        productRepo.deleteAll();
        userRepo.deleteAll();

        commandLineRunner.run();
    }
}
