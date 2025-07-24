plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.lan.campsiteproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lan.campsiteproject"
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
    implementation(libs.play.services.auth.v2070)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.v2231)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.volley)
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre8")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
// Sử dụng phiên bản mới nhất
    implementation("com.google.android.gms:play-services-base:18.5.0")
// Thêm nếu cần
    implementation("com.google.firebase:firebase-core:21.1.1")
// Nếu muốn SwipeRefreshLayout:
// implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
// Nếu muốn Material Components:
// implementation 'com.google.android.material:material:1.9.0'
}