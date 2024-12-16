package com.teamresourceful.publishing

import org.gradle.api.publish.maven.MavenPublication

interface ResourcefulPublishingPom {
    fun create(publication: MavenPublication)
}

data class GitHubPom(
    val name: String,
    val description: String,
    val license: String,
    val url: String
) : ResourcefulPublishingPom {
    override fun create(publication: MavenPublication) {
        publication.pom {
            this.name.set(name)
            this.description.set(description)
            this.url.set(url)

            scm {
                this.connection.set("git:$url.git")
                this.developerConnection.set("git:$url.git")
                this.url.set(url)
            }

            licenses {
                license {
                    this.name.set(license)
                }
            }
        }
    }
}