plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":util"))
    implementation(project(":tenant"))
    implementation(project(":company"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}