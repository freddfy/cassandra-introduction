import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
}

group = "me.fredd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.nosan:embedded-cassandra:2.0.2")
    implementation("com.datastax.cassandra:cassandra-driver-core:3.7.1")
    implementation("com.datastax.cassandra:cassandra-driver-mapping:3.7.1")
    implementation("org.slf4j:slf4j-simple:1.7.26")
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.nosan:embedded-cassandra-test:2.0.2")
    testImplementation("com.natpryce:hamkrest:1.7.0.0")

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}