plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.quacky20.watchdog"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.quacky20.watchdog"
        minSdk = 19
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}