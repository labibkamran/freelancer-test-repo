plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":company-lookup"))
    
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    // implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.flywaydb:flyway-core")
    // implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // runtimeOnly("org.postgresql:postgresql")
}