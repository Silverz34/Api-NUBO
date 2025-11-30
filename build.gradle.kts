plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    id("com.gradleup.shadow") version "9.2.2"
}

group = "infrastructure"
version = "0.0.1"

val ktor_version = "3.3.2"

application {
    mainClass = "infrastructure.ApplicationKt"
}

tasks {
    shadowJar {
        // Esto evita conflictos con nombres de archivos duplicados en librerías
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "infrastructure.ApplicationKt"))
        }
        // Opcional: Nombre fijo para no tener versiones en el nombre del archivo
        archiveFileName.set("mi-apiNubo.jar")
    }
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.postgresql:postgresql:42.7.7")


}