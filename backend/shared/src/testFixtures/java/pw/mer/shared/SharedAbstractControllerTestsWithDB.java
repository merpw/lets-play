package pw.mer.shared;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * Like {@link SharedAbstractControllerTestsBase}, but with a MongoDB container.
 */
public abstract class SharedAbstractControllerTestsWithDB extends SharedAbstractControllerTestsBase {
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
}
