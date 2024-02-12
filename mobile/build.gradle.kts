plugins {
    id("com.android.application")
}

android {
    namespace = "com.bely.kongplayer"
    compileSdk = 30

    defaultConfig {
        applicationId = "com.bely.kongplayer"
        minSdk = 28
        targetSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(mapOf("path" to ":shared")))
    implementation ("androidx.annotation:annotation:1.3.0")

    implementation ("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.google.android.material:material:1.4.0")

    androidTestImplementation ("androidx.test:core:1.4.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    implementation ("androidx.media:media:1.3.0")

    testImplementation("junit:junit:4.13.2")
}