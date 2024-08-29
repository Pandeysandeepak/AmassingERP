import java.util.regex.Pattern.compile


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.webview"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.webview"
        minSdk = 24
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["appAuthRedirectScheme"] = "https"

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
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }




    dependencies {
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.androidx.swiperefreshlayout)
        implementation(libs.androidx.games.activity)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
        implementation(libs.firebase.core)
        implementation(libs.play.services.auth)
        implementation(libs.play.services.games)
        implementation(libs.google.auth.library.oauth2.http.v122)
        implementation(libs.google.firebase.analytics)
        implementation(libs.firebase.messaging.v2310)
        implementation (libs.play.services.auth.v2050)
        implementation (libs.appauth.v0100)
        implementation (libs.converter.gson)
        debugImplementation (libs.library)
        releaseImplementation (libs.library.no.op)
        implementation (libs.google.http.client.gson)
        implementation (libs.play.services.identity)
        implementation(libs.volley)
        implementation(libs.dexter)


    }
}