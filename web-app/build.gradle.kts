plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":company-lookup"))
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":tenant"))
    implementation(project(":company"))
    implementation(project(":ledger"))
    implementation(project(":contact"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("io.github.wimdeblauwe:htmx-spring-boot:4.0.1")
    implementation("io.github.wimdeblauwe:htmx-spring-boot-thymeleaf:4.0.1")

    implementation("com.itextpdf:itext7-core:9.2.0")
    implementation("org.apache.pdfbox:pdfbox:2.0.29")
    implementation("net.sourceforge.tess4j:tess4j:5.8.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}