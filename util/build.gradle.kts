plugins {
    id("org.springframework.boot")
}

springBoot {
    buildInfo()
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.javamoney:moneta:1.4.5")
    runtimeOnly("org.postgresql:postgresql")
}