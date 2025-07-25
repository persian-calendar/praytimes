import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
}

group = "io.github.persiancalendar"
version = "3.1.2"

repositories {
    mavenCentral()
}

dependencies {
    val junit5Version = "5.13.4"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

val javaVersion = JavaVersion.VERSION_21
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.majorVersion)
    }
}

val sourceJar by tasks.creating(Jar::class) {
    dependsOn(tasks["classes"])
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            artifact(sourceJar)
        }
    }
}
