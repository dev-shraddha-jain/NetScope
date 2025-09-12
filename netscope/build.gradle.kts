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
            register<MavenPublication>("release") {
                groupId = "com.groot.netscope" // Replace with your desired group ID
                artifactId = "netscope" // Replace with your desired artifact ID
                version = "1.0" // Replace with your desired version

                from(components["release"])
            }
        }
        repositories {
            mavenLocal()
             maven {
                 name = "GitHubPackages"
                 url = uri("https://maven.pkg.github.com/dev-shraddha-jain/NetScope") // Updated URL
                 credentials {
                     username = "dev-shraddha-jain" // Your GitHub username directly
                     password = "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" // Your GitHub PAT from env variable
                 }
             }
        }
    }
}