plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.charizard.unikey"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.charizard.unikey"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}
