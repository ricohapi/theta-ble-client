import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish") version "0.32.0"
}

val theta_ble_version = "1.3.1"

group = "com.ricoh360.thetableclient"
version = theta_ble_version

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "THETA BLE Client"
        homepage = "https://github.com/ricohapi/theta-ble-client"
        name = "THETABleClient"
        authors = "Ricoh Co, Ltd."
        version = theta_ble_version
        source = "{ :http => 'https://github.com/ricohapi/theta-ble-client/releases/download/${theta_ble_version}/THETABleClient.xcframework.zip' }"
        license = "MIT"
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "THETABleClient"
            isStatic = false
        }
    }

    sourceSets {
        val coroutines_version = "1.7.3"

        val commonMain by getting {
            dependencies {
                implementation("com.juul.kable:core:0.27.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
                implementation("com.goncalossilva:resources:0.4.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:1.1.1")
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.ricoh360.thetableclient"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
        setProperty("archivesBaseName", "theta-ble-client")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(group.toString(), "theta-ble-client", version.toString())
    pom {
        name.set("theta-ble-client")
        description.set("This library provides a way to control RICOH THETA using RICOH THETA Bluetooth API")
        inceptionYear.set("2023")
        url.set("https://github.com/ricohapi/theta-ble-client")
        licenses {
            license {
                name.set("MIT")
                url.set("https://github.com/ricohapi/theta-ble-client/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                organization.set("RICOH360")
                organizationUrl.set("https://github.com/ricohapi/theta-ble-client")
            }
        }
        scm {
            connection.set("scm:git:git@github.com:ricohapi/theta-ble-client.git")
            developerConnection.set("scm:git:git@github.com:ricohapi/theta-ble-client.git")
            url.set("https://github.com/ricohapi/theta-ble-client/tree/main")
        }
    }
    /* Secrets
     *     Set following environment variables for Central Portal user token
     *       * ORG_GRADLE_PROJECT_mavenCentralUsername: username of the user token of Central Portal
     *       * ORG_GRADLE_PROJECT_mavenCentralPassword: password of the user token of Central Portal
     *     Set following environment variables for GPG key. See https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets
     *       * ORG_GRADLE_PROJECT_signingInMemoryKey : Secret key in PEM format
     *       * ORG_GRADLE_PROJECT_signingInMemoryKeyId : 8 characters key id
     *       * ORG_GRADLE_PROJECT_signingInMemoryKeyPassword
     */
}

tasks.dokkaHtml.configure {
    moduleName.set("theta-ble-client")

    if(project.properties["version"].toString() != theta_ble_version) {
        throw GradleException("The release version does not match the version defined in Gradle.")
    }

    val pagesDir = file(project.properties["workspace"].toString()).resolve("gh-pages")
    val currentVersion = theta_ble_version
    val currentDocsDir = pagesDir.resolve("docs")
    val docVersionsDir = pagesDir.resolve("version")
    outputDirectory.set(currentDocsDir)

    pluginConfiguration<org.jetbrains.dokka.versioning.VersioningPlugin, org.jetbrains.dokka.versioning.VersioningConfiguration> {
        version = currentVersion
        olderVersionsDir = docVersionsDir
    }

    doLast {
        val storedDir = docVersionsDir.resolve(currentVersion)
        currentDocsDir.copyRecursively(storedDir)
        storedDir.resolve("older").deleteRecursively()
    }
}
