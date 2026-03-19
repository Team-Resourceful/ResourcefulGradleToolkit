package com.teamresourceful.plugins

import com.teamresourceful.utils.*
import com.teamresourceful.utils.Platform
import org.gradle.kotlin.dsl.*

plugins {
    java
    id("net.neoforged.moddev")
    id("com.teamresourceful.plugins.minecraft")
}

val mcVersion = project.getMcVersion()

neoForge {
    neoFormVersion = project.getDependencyVersion("neoform") ?: throw Exceptions.missingVersion("neoform")
}

dependencies {
    compileOnly(group = "org.spongepowered", name = "mixin", version = "0.8.5")
    compileOnly(group = "io.github.llamalad7", name = "mixinextras-common", version = "0.3.5")
    annotationProcessor(group = "io.github.llamalad7", name = "mixinextras-common", version = "0.3.5")
}