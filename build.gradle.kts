import ca.solostudios.nyx.util.codeMC
import ca.solostudios.nyx.util.reposiliteMaven
import ca.solostudios.nyx.util.soloStudios
import ca.solostudios.nyx.util.soloStudiosSnapshots

plugins {
    `java-library`
    `maven-publish`

    alias(libs.plugins.nyx)
    alias(libs.plugins.axion.release)
    alias(libs.plugins.jmh)
}

nyx {
    info {
        name = "Paralithic"
        group = "com.dfsek"
        module = "paralithic"
        version = scmVersion.version
        description = """
            Paralithic is a super fast expression evaluator/parser written in Javay.
        """.trimIndent()

        organizationName = "Polyhedral Development"
        organizationUrl = "https://github.com/PolyhedralDev/"

        repository.fromGithub("PolyhedralDev", "Paralithic")

        license.useMIT()

        developer {
            id = "dfsek"
            name = "dfsek"
            email = "dfsek@dfsek.com"
            url = "https://dfsek.com/"
        }
        developer {
            id = "astrsh"
            name = "Astrash"
            email = "astrash@atr.sh"
            url = "https://atr.sh/"
        }
        developer {
            id = "duplexsystem"
            name = "Zoë Gidiere"
            email = "duplexsys@protonmail.com"
            url = "https://duplexsystem.org/"
        }
    }

    compile {
        jvmTarget = 21

        javadocJar = true
        sourcesJar = true

        allWarnings = true
        distributeLicense = true
        buildDependsOnJar = true
        reproducibleBuilds = true

        java {
            allJavadocWarnings = true
            noMissingJavadocWarnings = true
            javadocWarningsAsErrors = true
        }
    }

    publishing {
        withSignedPublishing()

        repositories {
            maven {
                name = "Sonatype"

                val repositoryId: String? by project
                url = when {
                    repositoryId != null -> uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/")
                    else -> uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }

                credentials(PasswordCredentials::class)
            }
            maven("https://repo.codemc.io/repository/maven-releases/") {
                name = "CodeMC"
                credentials(PasswordCredentials::class)
            }
            reposiliteMaven("https://maven.solo-studios.ca/releases/") {
                name = "SoloStudiosReleases"
                credentials(PasswordCredentials::class)
            }
            reposiliteMaven("https://maven.solo-studios.ca/snapshots/") {
                name = "SoloStudiosSnapshots"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    soloStudios()
    soloStudiosSnapshots()
    codeMC()
}

dependencies {
    implementation(libs.jetbrains.annotations)
    implementation(libs.slf4j.api)
    implementation(libs.seismic)

    api(libs.asm)

    testImplementation(libs.bundles.junit)
    testImplementation(libs.logback.classic)
    jmh(libs.parsii)
    jmh(libs.exp4j)
    jmh(libs.logback.classic)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.isFork = true
        options.isIncremental = true
    }

    withType<Test>().configureEach {
        useJUnitPlatform()

        maxHeapSize = "2G"
        ignoreFailures = false
        failFast = true
        maxParallelForks = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1)
    }
}

