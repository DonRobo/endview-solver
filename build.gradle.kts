plugins {
    kotlin("jvm") version "2.2.10"
    application
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "3.2.3"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("org.openjdk.nashorn:nashorn-core:15.7")

    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")

    implementation("org.jdatepicker:jdatepicker:1.3.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("at.robbert.RunMainApplicationKt")
}
