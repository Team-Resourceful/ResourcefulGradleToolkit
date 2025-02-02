plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "com.teamresourceful"
version = "1.0.3"

java.withSourcesJar()

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.architectury.dev")
    maven("https://maven.teamresourceful.com/repository/maven-public")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    compileOnly(libs.ktgradle)

    api(libs.archloom)
    api(libs.archplugin)
    api(libs.shadow)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            this.name.set("Resourceful Gradle Toolkit")
            this.description.set("Utilities made by Team Resourceful for gradle")
            this.url.set("https://github.com/Team-Resourceful/ResourcefulGradleToolkit")

            scm {
                this.connection.set("git:https://github.com/Team-Resourceful/ResourcefulGradleToolkit.git")
                this.developerConnection.set("git:https://github.com/Team-Resourceful/ResourcefulGradleToolkit.git")
                this.url.set("https://github.com/Team-Resourceful/ResourcefulGradleToolkit")
            }

            licenses {
                license {
                    this.name.set("MIT")
                }
            }
        }
    }

    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}