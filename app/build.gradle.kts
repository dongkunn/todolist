plugins {
    alias(libs.plugins.androidApplication)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dk.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dk.todolist"
        minSdk = 26
        targetSdk = 35
        versionCode = 18
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "AD_MAIN_UNIT_ID", "\"ca-app-pub-1417793607999454/4579677405\"")
        }

        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "AD_MAIN_UNIT_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    flavorDimensions += listOf("version")
    productFlavors {
        create("googleads") {
            dimension = "version"
            buildConfigField("boolean", "isGoogleAd", "true")
        }

        create("googlepaid") {
            dimension = "version"
            applicationIdSuffix = ".googlepaid"
            versionNameSuffix = "-googlepaid"
            buildConfigField("boolean", "isGoogleAd", "false")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.gms:play-services-ads:23.1.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.github.hearsilent:DiscreteSlider:1.2.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}