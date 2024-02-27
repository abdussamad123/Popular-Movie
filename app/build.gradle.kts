import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.popularmovie"
    compileSdk = 34

    val  keystorePropertiesFile = rootProject.file("keystore.properties")
    val projectProperties = readProperties(keystorePropertiesFile)

    defaultConfig {
        applicationId = "com.popularmovie"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", projectProperties["BASE_URL_PROD"] as String)
            buildConfigField("String", "MOVIE_DB_API_KEY", projectProperties["MOVIE_DB_API_KEY"] as String)
            buildConfigField("String", "MOVIE_IMAGE_URL", projectProperties["MOVIE_IMAGE_URL"] as String)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            buildConfigField("String", "BASE_URL", projectProperties["BASE_URL_DEV"] as String)
            buildConfigField("String", "MOVIE_DB_API_KEY", projectProperties["MOVIE_DB_API_KEY"] as String)
            buildConfigField("String", "MOVIE_IMAGE_URL", projectProperties["MOVIE_IMAGE_URL"] as String)
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

    hilt {
        enableAggregatingTask = true
    }
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}

dependencies {
    val lifecycle = "2.7.0"
    val coroutines = "1.6.4"
    val hiltVersion = "2.50"
    val retrofit = "2.9.0"
    val okhttpVersion = "4.9.1"
    val gsonVersion = "2.9.0"
    val room = "2.5.0"
    val glide = "4.13.0"
    val activityVersion = "1.8.2"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:$   ")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation ("com.squareup.retrofit2:converter-gson:$gsonVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation("androidx.activity:activity-ktx:$activityVersion")

    //room
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    //Glide
    implementation ("com.github.bumptech.glide:glide:$glide")
    annotationProcessor("com.github.bumptech.glide:compiler:$glide")


}
kapt {
    correctErrorTypes = true
}