import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.graalvm.buildtools.native") version "0.9.13"
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

graalvmNative {
    toolchainDetection.set(false)

    binaries {
        named("main") {
            mainClass.set("at.robbert.RunMainApplicationKt")
        }
    }
}

val ktorVersion = "2.1.0"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("org.openjdk.nashorn:nashorn-core:15.4")

    implementation("com.google.code.gson:gson:2.9.1")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")

    implementation("org.jdatepicker:jdatepicker:1.3.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
