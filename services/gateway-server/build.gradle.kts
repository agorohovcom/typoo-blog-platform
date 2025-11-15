plugins {
    id("org.springframework.boot")
    id("java")
}

dependencies {
    implementation(project(":shared"))

    // Spring Cloud
    implementation(libs.spring.cloud.starter.gateway.server.webflux)
    implementation(libs.spring.cloud.starter.loadbalancer)
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.config)

    // Spring Boot Starters
    implementation(libs.spring.boot.starter.actuator)

    // Utilities
    implementation(libs.caffeine)
    implementation(libs.micrometer.registry.prometheus)

    // Test
    testImplementation(libs.spring.boot.starter.test)

    // Annotation Processors
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

springBoot {
    mainClass.set("com.agorohov.typoo.gatewayserver.GatewayServerApplication")
}