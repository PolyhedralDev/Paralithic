plugins {
    java
}

group = "com.dfsek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }

}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    implementation("net.jafama:jafama:2.3.2")
    implementation("org.ow2.asm:asm:9.0")
    implementation("commons-io:commons-io:2.8.0")

    testImplementation("com.scireum:parsii:1.2.1")
    testImplementation("net.objecthunter:exp4j:0.4.8")
    testImplementation("junit", "junit", "4.12")
}
