import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    `maven-publish`
}

val versionObj = Version("0", "8", "0", false)


group = "com.dfsek"
version = versionObj

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")

    api("org.ow2.asm:asm:9.5")

    testImplementation("com.scireum:parsii:4.0")
    testImplementation("net.objecthunter:exp4j:0.4.8")
    testImplementation("junit:junit:4.13.2")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// Turn off javadoc linting temporarily to get 0.8.0 CI to build
tasks {
    javadoc {
        options {
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
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