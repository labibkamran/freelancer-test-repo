plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":company-lookup"))
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":tenant"))
    implementation(project(":company"))
    implementation(project(":account"))
    
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    //implementation("org.springframework.boot:spring-boot-starter-validation")
    //implementation("org.springframework.session:spring-session-jdbc")
}