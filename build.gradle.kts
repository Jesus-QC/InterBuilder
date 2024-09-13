plugins {
    id("java")
    kotlin("jvm") version "1.9.23"
    id("application")
    id("com.gradleup.shadow") version "8.3.1"
}

group = "com.intercord"
version = "1.0.0"

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(files("libs/gplayapi-3.4.2.jar"))
    implementation(files("libs/lspatch-398.jar"))
    implementation(files("libs/APKEditor-1.3.9.jar"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.protobuf:protobuf-java:4.26.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.apache.commons:commons-io:1.3.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "com.intercord.builder.Main"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

tasks.test {
    useJUnitPlatform()
}