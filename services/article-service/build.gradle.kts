plugins {
    id("org.springframework.boot")
    id("java")
}

dependencies {
    implementation(project(":shared"))

    // Spring Boot Starters
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)

    // Spring Cloud - ВСЕ через libs
    implementation(libs.spring.cloud.starter.consul.discovery)
    implementation(libs.spring.cloud.starter.config)

    // Database & Utilities
    implementation(libs.postgresql)
    implementation(libs.liquibase.core)
    implementation(libs.caffeine)
    implementation(libs.micrometer.registry.prometheus)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)

    // Annotation Processors
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
}

springBoot {
    mainClass.set("com.agorohov.typoo.article.ArticleServiceApplication")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(
        listOf(
            "-Amapstruct.defaultComponentModel=spring",
            "-Amapstruct.unmappedTargetPolicy=IGNORE"
        )
    )
}