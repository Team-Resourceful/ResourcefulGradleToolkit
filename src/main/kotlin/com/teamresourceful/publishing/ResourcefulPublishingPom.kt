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
        val data = this
        publication.pom {
            this.name.set(data.name)
            this.description.set(data.description)
            this.url.set(data.url)

            scm {
                this.connection.set("git:$data.url.git")
                this.developerConnection.set("git:$data.url.git")
                this.url.set(data.url)
            }

            licenses {
                license {
                    this.name.set(data.license)
                }
            }
        }
    }
}