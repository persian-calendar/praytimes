import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.4.20"
    `maven-publish`
}

group = (findProperty("group") as? String) ?: "io.github.persiancalendar"
version = (findProperty("version") as? String) ?: "4.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    js {
        nodejs()
        browser()
    }

    linuxX64()
    linuxArm64()
    macosArm64()
    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

tasks.withType<org.gradle.api.tasks.testing.AbstractTestTask>().configureEach {
    testLogging {
        showStandardStreams = true
    }
}
