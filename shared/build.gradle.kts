plugins {
    id("java-library")
}

dependencies {
    api(libs.spring.boot.starter)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}