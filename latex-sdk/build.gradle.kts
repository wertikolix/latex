import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    id("com.vanniktech.maven.publish") version "0.35.0"
}

kotlin {
    jvmToolchain(21)

    androidLibrary {
        namespace = "com.hrm.latex.sdk"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {}
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "LatexSDK"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.latexBase)
            api(projects.latexParser)
            api(projects.latexRenderer)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


mavenPublishing {
    publishToMavenCentral(true)

    signAllPublications()

    coordinates("io.github.huarangmeng", "latex", rootProject.property("VERSION").toString())

    pom {
        name = "Kotlin Multiplatform LaTeX Rendering Engine"
        description = """
            Cross-platform LaTeX math rendering solution with:
            - Full LaTeX syntax support (math mode)
            - Custom command definitions
            - Chemical formula rendering
            - Compose Multiplatform UI integration
            - Multi-module architecture (base/parser/renderer)
        """
        inceptionYear = "2026"
        url = "https://github.com/huarangmeng/latex"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "huaranmeng"
                name = "Kotlin Multiplatform Specialist"
                url = "https://github.com/huarangmeng/"
            }
        }
        scm {
            url = "https://github.com/huarangmeng/latex"
            connection = "scm:git:git://github.com/huarangmeng/latex.git"
            developerConnection = "scm:git:ssh://git@github.com/huarangmeng/latex.git"
        }
    }
}