import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "rs.ac.bg.etf.dm200157d"
    compileSdk = 34

    defaultConfig {
        applicationId = "rs.ac.bg.etf.dm200157d"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties["TMDB_API_KEY"]}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(files("libs/mdjlibrary-release.aar"))
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer.hls)
    androidTestImplementation(libs.android.test)
    androidTestImplementation(libs.espresso)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    testImplementation(libs.robolectric)
}

kapt {
    correctErrorTypes = true
}