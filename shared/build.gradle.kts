plugins {
    id("java-library")
}

dependencies {
    api(libs.spring.boot.starter)
    api(libs.spring.web)
    compileOnly(libs.jakarta.servlet)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}