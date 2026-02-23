rootProject.name = "typoo-blog-platform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("org.springframework.boot") version "${extra["springBootVersion"]}"
        id("io.spring.dependency-management") version "${extra["springDependencyManagementVersion"]}"
    }
}

// Включаем только те модули, чьи директории существуют
val modules = listOf("config-server", "gateway-server", "article-service")

modules.forEach { moduleName ->
    val moduleDir = file("services/$moduleName")
    if (moduleDir.exists() && moduleDir.isDirectory) {
        include(":services:$moduleName")
    } else {
        println("Skipping module '$moduleName' - directory not found")
    }
}

include(":shared")