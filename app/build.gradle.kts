plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.trabalho4"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.trabalho4"
        minSdk = 24
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
}

dependencies {
    // 1. OSMBonusPack e Osmdroid (conforme projeto MeuMapa)
    implementation(files("libs/osmbonuspack_6.9.0.aar"))
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // 2. Localização GPS (Slide 13 de Permissões)
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // 3. CameraX (Slide 15 de Permissões)
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")

    // 4. Carregamento de imagens na galeria
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}