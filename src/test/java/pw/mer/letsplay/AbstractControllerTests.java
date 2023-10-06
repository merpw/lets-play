package pw.mer.letsplay;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import pw.mer.letsplay.repository.ProductRepo;
import pw.mer.letsplay.repository.UserRepo;

import static pw.mer.letsplay.AuthFactory.getAccessToken;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTests {
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
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    public String getAdminToken() {
        String adminEmail = env.getProperty("initial.admin.email");
        String adminPassword = env.getProperty("initial.admin.password");

        return getAccessToken(adminEmail, adminPassword);
    }

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
