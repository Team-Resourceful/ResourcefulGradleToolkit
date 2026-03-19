package com.teamresourceful.utils

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.*

enum class Platform {
    COMMON,
    FABRIC,
    NEOFORGE,
    ;

    val id: String
        get() = name.lowercase(Locale.ROOT)
}

fun Project.getPlatformOrNull(): Platform? {
    val platform: String? = this.rootProject.findProperty("mc.platform")?.toString()
    val name = this.name.lowercase(Locale.ROOT)
    return when (platform?.lowercase(Locale.ROOT)) {
        "common" -> Platform.COMMON
        "fabric" -> Platform.FABRIC
        "neoforge" -> Platform.NEOFORGE
        else -> when {
            "common" in name -> Platform.COMMON
            "fabric" in name -> Platform.FABRIC
            "neoforge" in name -> Platform.NEOFORGE
            else -> null
        }
    }
}

fun Project.getPlatform(): Platform {
    return this.getPlatformOrNull() ?: throw GradleException("Could not infer platform from project name or `mc.platform` property for project '$name'.")
}