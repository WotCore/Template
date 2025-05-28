import wot.buildconfig.AndroidConfig
import wot.buildconfig.BuildFields
import wot.buildconfig.JavaConfig
import wot.buildconfig.Modules
import wot.buildconfig.ProductFlavors
import wot.deps.DepsConfig
import wot.deps.DepsPlugin

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

apply<DepsPlugin>()

extensions.configure<DepsConfig> {
    profile = DepsConfig.Profile.FULL

    navigation(false)
    room(false)
}

android {
    namespace = "wot.core.demo.template"
    compileSdk = AndroidConfig.compileSdk

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "wot.core.demo.template"
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        versionCode = 10000
        versionName = "1.0.0"

        buildConfigField("Boolean", "ENABLE_LOGGING", BuildFields.ENABLE_LOGGING)

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

    flavorDimensions("version")
    productFlavors {
        create(ProductFlavors.dev) {
            dimension = "version" // 所属维度, version、api、environment
            applicationIdSuffix = ".dev" // applicationId加后缀, com.example.app -> com.example.app.dev
            versionNameSuffix = "-dev" // versionName加后缀, 1.0.0 -> 1.0.0-dev
        }
        create(ProductFlavors.prod) {
            dimension = "version"
            // 生产版默认配置
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaConfig.jvmTarget
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)

    implementation(project(Modules.template))
}