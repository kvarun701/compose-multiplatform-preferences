import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
    signing
}

group = "io.github.kvarun701"
version = "1.0.0"

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    androidLibrary {
       namespace = "com.ganesh.composepref.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
localProperties.forEach { key, value ->
    extra.set(key.toString(), value.toString())
}

publishing {
    publications.withType<MavenPublication> {
        artifactId = artifactId.replace("shared", "compose-pref")
        
        val javadocJarTask = tasks.register("javadocJarFor$name", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(name)
        }
        artifact(javadocJarTask)
        
        pom {
            name.set("Compose Multiplatform Preferences")
            description.set("A lightweight Key-Value Storage library for Kotlin Multiplatform and Compose Multiplatform projects.")
            url.set("https://github.com/kvarun701/compose-multiplatform-preferences")
            
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("kvarun701")
                    name.set("Varun")
                    email.set("kvarun701@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/kvarun701/compose-multiplatform-preferences.git")
                developerConnection.set("scm:git:ssh://github.com/kvarun701/compose-multiplatform-preferences.git")
                url.set("https://github.com/kvarun701/compose-multiplatform-preferences")
            }
        }
    }
    
    repositories {
        maven {
            name = "Staging"
            url = uri(layout.buildDirectory.dir("staging-repository"))
        }
        maven {
            name = "Sonatype"
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments")
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                    ?: localProperties.getProperty("sonatypeUsername")
                    ?: project.findProperty("sonatypeUsername")?.toString()
                password = System.getenv("SONATYPE_PASSWORD")
                    ?: localProperties.getProperty("sonatypePassword")
                    ?: project.findProperty("sonatypePassword")?.toString()
            }
        }
    }
}

signing {
    val isSigningRequired = project.hasProperty("signing.keyId")
        || project.hasProperty("signing.key")
        || System.getenv("SIGNING_KEY") != null
    if (isSigningRequired) {
        val signingKey = System.getenv("SIGNING_KEY")
            ?: project.findProperty("signing.key")?.toString()
        val signingPassword = System.getenv("SIGNING_PASSWORD")
            ?: project.findProperty("signing.password")?.toString()
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(publishing.publications)
    }
}