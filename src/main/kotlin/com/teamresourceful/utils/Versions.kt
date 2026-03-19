package com.teamresourceful.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

private const val VERSION_MINECRAFT = "minecraft"

fun Project.getMcVersion(): String = this.rootProject.extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(VERSION_MINECRAFT)
    .orElseThrow { Exceptions.missingVersion(VERSION_MINECRAFT) }
    .requiredVersion

/**
 * Will try to return the version catalog for the dependency if not found will try version
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

fun Project.getDependencyVersion(id: String): String? {
    val libs = this.rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    return libs.findVersion(id).map(VersionConstraint::getRequiredVersion).orElse(null)
}