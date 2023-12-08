plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.warroomapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.warroomapp"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //Streaming CCTV via StreamHttpResponse
    implementation("com.google.android.exoplayer:exoplayer:2.19.1");
    implementation("androidx.media3:media3-exoplayer:1.1.1");
    implementation("androidx.media3:media3-ui:1.1.1");
    implementation("androidx.media3:media3-exoplayer-dash:1.1.1");
    //Load LottieJson Animation file
    implementation("com.airbnb.android:lottie:6.1.0");
    //WebSocket
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0");
    implementation("org.java-websocket:Java-WebSocket:1.5.4");
    implementation("com.squareup.picasso:picasso:2.8");
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}