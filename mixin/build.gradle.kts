plugins {
    id("java")
}

group = "ru.astemir.simplehunger"
version = "1.0-SNAPSHOT"

val userHome = System.getProperty("user.home")
val hytaleServerPath = "$userHome/Desktop/HytaleServer"
val hytaleDownloadDir = "C:/HytaleServerDownloader/2026.01.13-dcad8778f/Server"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("$hytaleServerPath/earlyplugins/Hyxin-0.0.11-all.jar"))
    compileOnly(files("$hytaleDownloadDir/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    destinationDirectory.set(file("$hytaleServerPath/earlyplugins"))
    archiveFileName.set("SimpleHunger-Mixin-${version}.jar")
}
