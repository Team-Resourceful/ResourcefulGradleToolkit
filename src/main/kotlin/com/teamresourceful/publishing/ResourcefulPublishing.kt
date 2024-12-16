package com.teamresourceful.publishing

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

fun Project.javaPublishing(action: ResourcefulPublishing.() -> Unit) {
    val data = ResourcefulPublishing().also(action)
    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            publications.create<MavenPublication>("maven") {
                artifactId = data.artifactId
                from(components["java"])
                data.pom?.create(this)
            }

            repositories {
                maven {
                    setUrl(data.repo)
                    credentials {
                        username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                        password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
                    }
                }
            }
        }
    }
}

data class ResourcefulPublishing(
    var artifactId: String = "",
    var pom: ResourcefulPublishingPom? = null,
    var repo: String = "",
)