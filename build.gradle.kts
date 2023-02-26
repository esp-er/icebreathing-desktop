import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.3.0"
    id("dev.hydraulic.conveyor") version "1.4"
}

group = "me.patrik"
version = "1.0"

val korauVersion = "2.7.0"
val korioVersion = "2.7.0"


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}


configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

dependencies {
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.1")
    implementation("com.soywiz.korlibs.korau:korau-jvm:$korauVersion")
    implementation("com.soywiz.korlibs.korio:korio-jvm:$korioVersion")
}


compose.desktop {
    application {
        mainClass = "io.github.esp_er.icebreathing.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Ice Breathing"
            description = "An Iceman breathing meditation application"
            copyright = "(c) Patrik Eriksson"
            vendor = "esp-er.github.io"
            packageVersion = "1.0.1"

            macOS {
                bundleID = "io.github.esp_er.icebreathing"
                packageVersion = "1.0.1"
                dmgPackageVersion = "1.0.0"
                iconFile.set(project.file("icebreathing.icns"))
            }
            windows {
                packageVersion = "1.0.1"
                msiPackageVersion = "1.0.0"
            }


        }

        jvmArgs += "-Xmx120M"
    }
}

