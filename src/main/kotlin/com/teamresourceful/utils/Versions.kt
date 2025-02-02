package com.teamresourceful.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

private const val VERSION_MINECRAFT = "minecraft"
private const val VERSION_PARCHMENT = "parchment"
private const val PARCHMENT_MAVEN_PREFIX = "org.parchmentmc.data:parchment-"

fun Project.getMcVersion(): String = this.rootProject.extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(VERSION_MINECRAFT)
    .orElseThrow { Exceptions.missingVersion(VERSION_MINECRAFT) }
    .requiredVersion

fun Project.getParchmentMavenCoordinate(): String? {
    val defaultVersion = this.rootProject.extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion(VERSION_PARCHMENT)
        .map { it.requiredVersion.takeIf(String::isNotBlank) }

    fun create(mcVersion: String, version: String): String =
        "${PARCHMENT_MAVEN_PREFIX}${mcVersion}:${defaultVersion.orElse(version)}@zip"

    return when (getMcVersion()) {
        "1.16.5" -> create("1.16.5", "2022.03.06")
        "1.17" -> create("1.17", "2021.07.21")
        "1.17.1" -> create("1.17", "2021.12.12")
        "1.18", "1.18.1" -> create("1.18.1", "2022.03.06")
        "1.18.2" -> create("1.18.2", "2022.11.06")
        "1.19", "1.19.1", "1.19.2" -> create("1.19.2", "2022.11.27")
        "1.19.3" -> create("1.19.3", "2023.06.25")
        "1.19.4" -> create("1.19.4", "2023.06.26")
        "1.20", "1.20.1" -> create("1.20.1", "2023.09.03")
        "1.20.2" -> create("1.20.2", "2023.12.10")
        "1.20.3" -> create("1.20.3", "2023.12.31")
        "1.20.4" -> create("1.20.4", "2024.04.14")
        "1.20.5", "1.20.6" -> create("1.20.6", "2024.06.16")
        "1.21" -> create("1.21", "2024.11.10")
        "1.21.1", "1.21.2" -> create("1.21.1", "2024.11.17")
        "1.21.3" -> create("1.21.1", "2024.12.07")
        "1.21.4" -> create("1.21.4", "2024.12.07")
        else -> null
    }
}

/**
 * Will try to return the version catalog for the dependency if not found will try version if that is not found will return `latest.release`
 */
fun Project.getDependencyVersion(
    id: String,
    dependencyOps: (Provider<MinimalExternalModuleDependency>) -> Unit,
    versionOps: (String?) -> Unit
) {
    val libs = this.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    libs.findLibrary(id).ifPresentOrElse(dependencyOps) {
        versionOps(libs.findVersion(id).map(VersionConstraint::getRequiredVersion).orElse(null))
    }
}