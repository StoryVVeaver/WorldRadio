plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
}

android {
    namespace = "by.roman.worldradio0"
    compileSdk = 36

    defaultConfig {
        applicationId = "by.roman.worldradio0"
        minSdk = 28
        versionCode = 1
        versionName = "0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.hilt.android)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.convertergson)
    implementation(libs.media3exoplayer)
    implementation(libs.media3dash)
    implementation(libs.media3hls)
    implementation(libs.media3session)
    implementation(libs.media3ui)
    implementation(libs.media)
    implementation(libs.ui.graphics.android)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.osmdroid)

    implementation(libs.palette)
    implementation(libs.core)
    implementation(libs.cardview)


    annotationProcessor(libs.glide.compiler)
    annotationProcessor(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}