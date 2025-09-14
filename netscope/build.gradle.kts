plugins {
    id("com.android.library") // Changed from alias and .application to .library
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    `maven-publish` // Added maven-publish plugin
}

android {
    namespace = "com.groot.netscope"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        javaToolchains {
            JavaLanguageVersion.of(17)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.nanohttpd:nanohttpd:2.3.1")


    // Add these Compose dependencies
    implementation(platform(libs.androidx.compose.bom)) // Import the BOM
    implementation(libs.androidx.ui)                     // For @Composable annotations and core runtime
//    implementation(libs.androidx.compose.runtime)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("maven") {
                groupId = "com.github.api.inceptor"
                artifactId = "NetScope"
                version = "1.0.0" // JitPack uses Git tags/releases for versioning by consumers

                from(components["release"])
            }
        }
        // repositories block removed for JitPack
    }
}