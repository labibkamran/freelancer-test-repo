plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":company-lookup"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("org.postgresql:postgresql")
}