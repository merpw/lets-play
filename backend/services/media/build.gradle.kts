dependencies {
    testImplementation(testFixtures(project(":shared")))
    implementation(project(":shared"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
}