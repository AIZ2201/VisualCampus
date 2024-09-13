import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.openjfx.javafxplugin").version("0.0.9")
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.fxml")
}


dependencies {
    // Compose dependencies
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.ui:ui:1.4.0")
    implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.4.0")
    implementation("org.jetbrains.compose.foundation:foundation:1.4.0")
    implementation("org.jetbrains.compose.material:material:1.4.0")
    implementation("org.jetbrains.compose.ui:ui-graphics:1.4.0")
    implementation("org.jetbrains.compose.material3:material3:1.0.0")

    // Kotlin and Coroutines
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")

    // JSON and Data libraries
    implementation("net.sf.json-lib:json-lib:2.4:jdk15")
    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-logging:commons-logging:1.2")
    implementation("net.sf.ezmorph:ezmorph:1.0.6")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    // Apache POI
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")

    implementation("org.openjfx:javafx-controls:17.0.1")
    // OpenCV
    implementation(files("D:\\opencv\\build\\java\\opencv-4100.jar"))
}

compose.desktop {
    application {
        mainClass = "example.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "demo3"
            packageVersion = "1.0.0"

            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
            }

            modules("jdk.crypto.ec")

            // Add DLL file copying logic
            afterEvaluate {
                val opencvDllDir = file("D:/opencv/build/x64/vc16/bin")
                copy {
                    // 复制 DLL 文件到与 exe 同级目录
                    from(opencvDllDir) {
                        include("*.dll")
                    }
                    into("${buildDir}/compose/binaries/main/app/demo3/") // 与 exe 文件同级

                    // 复制 OpenCV JAR 文件到与 exe 同级目录
                    from(file("D:/opencv/build/java")) {
                        include("*.jar")
                    }
                    into("${buildDir}/compose/binaries/main/app/demo3/") // 与 exe 文件同级

                    // 复制项目生成的 JAR 文件
                    from(fileTree("build/libs")) {
                        include("*.jar")
                    }
                    into("${buildDir}/compose/binaries/main/app/demo3/") // 与 exe 文件同级
                }
            }
        }
    }
}
