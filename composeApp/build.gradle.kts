plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    android {
        namespace = "com.example.wydatki.shared"
        compileSdk = 37
        minSdk = 24
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Wydatki shared Kotlin Multiplatform module"
        homepage = "https://github.com/janczesko12/Wydatki-IOS"
        ios.deploymentTarget = "15.0"

        pod("FirebaseCore")
        pod("FirebaseAuth")
        pod("FirebaseFirestore")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.firebase.common)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
        }
        androidMain.dependencies {
            implementation(libs.androidx.biometric)
            implementation(libs.androidx.activity.compose)
            implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
            implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
            implementation("com.google.firebase:firebase-common-ktx:21.0.0")
        }
        iosMain.dependencies {
        }
    }
}
