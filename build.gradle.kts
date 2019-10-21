
plugins {
    kotlin("multiplatform") version "1.3.50"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.50"
}

group = "me.undownding"
version = "1.0.1"

repositories {
    maven(url = "https://maven.aliyun.com/repository/jcenter")
    maven(url = "https://maven.aliyun.com/repository/central")
}

kotlin {
    mingwX64("native") {
        binaries {
            executable {
                linkerOpts("-fexec-charset=GBK", "-finput-charset=UTF-8")
            }
        }
        artifacts {
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.13.0")
                implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.15")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.2")
            }
        }
    }

}

tasks.withType<Wrapper> {
    gradleVersion = "4.10.3"
    distributionType = Wrapper.DistributionType.ALL
}


