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

include(":shared")
include(":services:config-server")
include(":services:gateway-server")
include(":services:article-service")