plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // WAJIB: Tambahkan plugin KSP untuk Room
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.tugasakhir_145"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tugasakhir_145"
        minSdk = 24
        targetSdk = 34
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
        // Compose is still enabled as plugin is applied, but valid to keep concurrently
        compose = true
        viewBinding = true
    }

    // Room memerlukan pemetaan schema (opsional tapi disarankan)
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
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

    // NAVIGATION
    implementation(libs.androidx.navigation.compose)

    // ROOM DATABASE
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Gunakan ksp, BUKAN kapt atau annotationProcessor
    ksp(libs.androidx.room.compiler)

    // IMAGE LOADING (Untuk foto produk)
    implementation(libs.coil.compose)

    // Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- View System Dependencies (Required for existing XML/Fragments) ---
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.coil)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}