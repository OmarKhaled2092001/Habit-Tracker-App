plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.habittrackerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.habittrackerapp"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth")


    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.facebook.android:facebook-android-sdk:18.0.3")

    implementation("com.airbnb.android:lottie-compose:6.6.6")

    implementation ("androidx.compose.material:material-icons-extended")

    implementation ("androidx.compose.material:material:1.7.8")
    implementation ("androidx.compose.foundation:foundation:1.7.8")
    implementation ("androidx.compose.material:material-icons-extended:1.7.8")

    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.firebase:firebase-firestore-ktx")

    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")
    implementation("androidx.compose.runtime:runtime:1.7.8")

    // Emoji Picker
    implementation("com.github.Abhimanyu14:compose-emoji-picker:1.0.0-alpha16")
    implementation("androidx.emoji2:emoji2:1.4.0")


}