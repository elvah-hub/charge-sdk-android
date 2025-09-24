plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.serializable)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "de.elvah.charge"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)

        @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
        abiValidation {
            enabled.set(true)
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "charge-sdk-android"
            groupId = "com.github.elvah-hub"
            version = "0.3.4"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}



tasks.register("checkMinifyEnabled") {
    doLast {
        if (android.buildTypes.getByName("release").isMinifyEnabled) {
            throw GradleException("Minification is enabled for the release build type. Please disable it.")
        }
    }
}

tasks.matching { it.group == "publishing" }.onEach {
    it.dependsOn("checkMinifyEnabled")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:21.0-rc-1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}


dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3)

    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)

    implementation(libs.bundles.networking)

    implementation(platform(libs.arrow.stack))
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.stripe.android)
    implementation(libs.coil3.coil.compose)

    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.fragment.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
}
