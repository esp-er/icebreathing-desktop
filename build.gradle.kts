import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.3.0-rc01"
}

group = "me.patrik"
version = "1.0"

val korauVersion = "2.7.0"
val korioVersion = "2.7.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.1")
    implementation("com.soywiz.korlibs.korau:korau-jvm:$korauVersion")
    implementation("com.soywiz.korlibs.korio:korio-jvm:$korioVersion")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}

compose.desktop {
    application {
        mainClass = "patriker.breathing.iceman.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "patriker.breathing.iceman"
            packageVersion = "1.0.0"
        }
    }
}

