plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":util"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    runtimeOnly("org.postgresql:postgresql")
}
