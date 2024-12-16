package com.teamresourceful.utils

import dev.architectury.plugin.ModLoader
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.*

enum class Platform {
    COMMON,
    FABRIC,
    NEOFORGE,
    ;

    val modLoader: ModLoader?
        get() = when (this) {
            FABRIC -> ModLoader.FABRIC
            NEOFORGE -> ModLoader.NEOFORGE
            else -> null
        }

    val archDevName: String?
        get() = when (this) {
            FABRIC -> "Fabric"
            NEOFORGE -> "NeoForge"
            else -> null
        }

    val id: String
        get() = this.name.toLowerCase(Locale.ROOT)
}

fun Project.getPlatform(): Platform {
    val platform: String? = this.rootProject.findProperty("loom.platform")?.toString()
    val name = this.name.toLowerCase(Locale.ROOT)
    return when (platform?.toLowerCase(Locale.ROOT)) {
        "common" -> Platform.COMMON
        "fabric" -> Platform.FABRIC
        "neoforge" -> Platform.NEOFORGE
        else -> when {
            "common" in name -> Platform.COMMON
            "fabric" in name -> Platform.FABRIC
            "neoforge" in name -> Platform.NEOFORGE
            else -> throw GradleException("Could not infer platform from project name or `loom.platform` property for project '$name'.")
        }
    }
}