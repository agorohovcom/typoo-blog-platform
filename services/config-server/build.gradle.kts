plugins {
    id("org.springframework.boot")
    id("java")
}

dependencies {
    implementation(project(":shared"))

    // Spring Boot Starters
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)

    // Spring Cloud
    implementation(libs.spring.cloud.config.server)
    implementation(libs.spring.cloud.starter.consul.discovery)

    // Test
    testImplementation(libs.spring.boot.starter.test)

    // Annotation Processors
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

springBoot {
    mainClass.set("com.agorohov.typoo.configserver.ConfigServerApplication")
}