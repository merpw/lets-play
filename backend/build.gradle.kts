plugins {
    java
    jacoco
    id("io.freefair.lombok") version "8.3"
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
}

allprojects {
    apply {
        plugin("java")
        plugin("jacoco")
        plugin("io.freefair.lombok")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    group = "pw.mer"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // always generate jacoco coverage report after running tests
    tasks.test {
        finalizedBy(tasks.jacocoTestReport)
        reports {
            junitXml.required = true
        }
    }
    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required = true
        }
    }
}