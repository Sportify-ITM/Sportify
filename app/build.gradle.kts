plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.sportify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sportify"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets", "src/main/res/drawable/LoginPages")
            }
        }
    }
}

dependencies {
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("com.google.firebase:firebase-firestore:24.0.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.prolificinteractive:material-calendarview:1.4.3")

    //파이어베이스
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")

    //파이어베이스 클라우드 메시징
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("com.squareup.okhttp3:okhttp:3.4.1")
    implementation("com.google.code.gson:gson:2.8.6")

    //커뮤니티 화면 이미지 업로더
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

    // Google Map
    implementation("com.google.android.gms:play-services-maps:18.2.0") // Google Play services SDK version
    implementation("com.google.android.gms:play-services-location:21.0.0")

    // Room dependencies
    implementation ("androidx.room:room-runtime:2.5.1")
    implementation ("androidx.room:room-ktx:2.5.1")
    kapt ("androidx.room:room-compiler:2.5.1")
    androidTestImplementation ("androidx.room:room-testing:2.5.1")
    kapt ("org.xerial:sqlite-jdbc:3.34.0")

    // chart dependency
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
}