import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.components.resources)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
            implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
            implementation("io.github.vinceglb:filekit-core:0.8.2")

            // Enables FileKit with Composable utilities
            implementation("io.github.vinceglb:filekit-compose:0.8.2")
            //implementation(libs.navigation.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.jetbrains.jediterm:jediterm-core:3.47")
            implementation("org.jetbrains.jediterm:jediterm-ui:3.47")
            implementation("org.slf4j:slf4j-api:2.0.0") // API do SLF4J
            implementation("ch.qos.logback:logback-classic:1.4.7") // Implementação do Logback

        }
    }
}



compose.desktop {
    application {
        mainClass = "com.pedrodev.jgol.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.pedrodev.jgol"
            packageVersion = "1.0.0"
        }
    }
}
