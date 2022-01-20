import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    `maven-publish`
}

val versionObj = Version("0", "6", "1", false)


group = "com.dfsek"
version = versionObj

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
}

dependencies {
    implementation("org.jetbrains:annotations:22.0.0")

    api("org.ow2.asm:asm:9.2")

    testImplementation("net.jafama:jafama:2.3.2")
    testImplementation("com.scireum:parsii:1.2.1")
    testImplementation("net.objecthunter:exp4j:0.4.8")
    testImplementation("junit:junit:4.12")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks["sourcesJar"])
            artifact(tasks["jar"])
        }
    }

    repositories {
        val mavenUrl = "https://repo.codemc.io/repository/maven-releases/"

        maven(mavenUrl) {
            val mavenUsername: String? by project
            val mavenPassword: String? by project
            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

/**
 * Version class that does version stuff.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Version(val major: String, val minor: String, val revision: String, val preRelease: Boolean = false) {

    override fun toString(): String {
        return if (!preRelease)
            "$major.$minor.$revision"
        else //Only use git hash if it's a prerelease.
            "$major.$minor.$revision+${getGitHash()}"
    }
}
fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = mutableListOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}