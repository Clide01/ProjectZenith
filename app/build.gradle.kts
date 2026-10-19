plugins {
    alias(libs.plugins.android.application)
}

import java.util.Properties

val signingPropertiesFile = rootProject.file("keystore.properties")
val signingProperties = Properties()
if (signingPropertiesFile.exists()) {
    signingPropertiesFile.inputStream().use(signingProperties::load)
}

android {
    namespace = "com.projectzenith.official"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.projectzenith.official"
        minSdk = 24
        targetSdk = 37
        versionCode = 27
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.create("release")
                .apply {
                    storeFile = rootProject.file(signingProperties.getProperty("storeFile", "keystore/projectzenith-release.jks"))
                    storePassword = signingProperties.getProperty("storePassword")
                    keyAlias = signingProperties.getProperty("keyAlias", "projectzenith-release")
                    keyPassword = signingProperties.getProperty("keyPassword")
                }
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    implementation("dev.rikka.shizuku:api:13.1.0")
    implementation("dev.rikka.shizuku:provider:13.1.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("com.unity3d.ads:unity-ads:4.20.1")
}
