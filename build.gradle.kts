plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.21"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // ou 21
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    implementation("org.jetbrains.exposed:exposed-core:0.35.3")
    implementation("org.jetbrains.exposed:exposed-dao:0.35.3")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.35.3")
    //implementation("org.jetbrains.exposed:exposed-core:0.43.0") // para java 11
    //#implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    //implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}

application {
    // Ponto de entrada do seu servidor
    mainClass.set("com.example.flashcardapp.ApplicationKt")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.example.flashcardapp.ApplicationKt"
        )
    }
    from(sourceSets.main.get().output)
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
