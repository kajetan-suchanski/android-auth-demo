plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Version.compileSdk)
    buildToolsVersion = Version.buildTools

    defaultConfig {
        applicationId = "pl.kajetansuchanski.demos.auth"
        minSdkVersion(Version.minSdk)
        targetSdkVersion(Version.compileSdk)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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

    sourceSets.all {
        java.srcDir("src/$name/kotlin")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")

    implementation("androidx.biometric:biometric:${Version.biometric}")

    // Kandy
    implementation("com.kwezal.kandy:dialogs:${Version.kandy}@aar")
    debugImplementation("com.kwezal.kandy:logs-debug:${Version.kandy}@aar")
    releaseImplementation("com.kwezal.kandy:logs-release:${Version.kandy}@aar")

    "testImplementation"("junit:junit:4.+")
    "androidTestImplementation"("androidx.test.ext:junit:1.1.2")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.3.0")
}