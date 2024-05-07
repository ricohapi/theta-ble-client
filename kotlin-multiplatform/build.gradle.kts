import java.util.*

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    signing
}

val theta_ble_version = "1.2.0"

group = "com.ricoh360.thetableclient"
version = theta_ble_version

// Init publish property
initProp()

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
        val androidMain by getting
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
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        setProperty("archivesBaseName", "theta-ble-client")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

// Publish the library to GitHub Packages Mavan repository.
// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    initProp()
    publishing {
        publications.withType(MavenPublication::class) {
            artifact(javadocJar.get())
            when (name) {
                "kotlinMultiplatform" -> {
                    setArtifactId("theta-ble-client")
                }
                "androidRelease" -> {
                    setArtifactId("theta-ble-client-android")
                }
                else -> {
                    setArtifactId("theta-ble-client-$name")
                }
            }
            pom {
                name.set("theta-ble-client")
                description.set("This library provides a way to control RICOH THETA using RICOH THETA Bluetooth API v2")
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
        }
        repositories {
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = getExtraString("ossrhUsername")
                    password = getExtraString("ossrhPassword")
                }
            }
        }
    }
}

signing {
    if (getExtraString("signing.keyId") != null) {
        useInMemoryPgpKeys(
            getExtraString("signing.keyId"),
            getExtraString("signing.key"),
            getExtraString("signing.password")
        )
        sign(publishing.publications)
    }
}

ext["signing.keyId"] = null
ext["signing.key"] = null
ext["signing.password"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

fun initProp() {
    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            Properties().apply {
                load(it)
            }
        }.onEach { (name, value) ->
            ext[name.toString()] = value
        }
    } else {
        ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        ext["signing.key"] = System.getenv("SIGNING_KEY")
        ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
        ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
        ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    }
}

fun getExtraString(name: String): String? {
    if (ext.has(name)) {
        return ext[name]?.toString()
    }
    return null
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
