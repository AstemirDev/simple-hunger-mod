plugins {
    id("java")
    id("java-library")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "ru.astemir.simplehunger"
version = "1.0-SNAPSHOT"
val userHome: String? = System.getProperty("user.home")
val hytaleDownloadDir = "C:/HytaleServerDownloader/2026.01.13-dcad8778f/Server"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("$hytaleDownloadDir/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    enabled = false
}

tasks.processResources {
    exclude("hotswap-agent.properties")
}

tasks.shadowJar {
    destinationDirectory.set(file("$userHome/Desktop/HytaleServer/mods"))
    archiveFileName.set("SimpleHunger-${version}.jar")
}

