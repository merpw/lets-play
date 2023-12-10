plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")

    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    testFixturesApi("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("org.springframework.security:spring-security-test")

    testFixturesApi("org.springframework.boot:spring-boot-testcontainers")
    testFixturesApi("io.rest-assured:rest-assured")
    testFixturesApi("org.testcontainers:junit-jupiter")
    testFixturesApi("org.testcontainers:mongodb:1.18.1")

    testFixturesApi("com.fasterxml.jackson.core:jackson-databind")
}

