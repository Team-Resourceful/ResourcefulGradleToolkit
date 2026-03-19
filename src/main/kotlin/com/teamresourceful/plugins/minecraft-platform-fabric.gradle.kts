package com.teamresourceful.plugins

import com.teamresourceful.utils.Exceptions
import com.teamresourceful.utils.getDependencyVersion
import com.teamresourceful.utils.getMcVersion
import org.gradle.kotlin.dsl.*

plugins {
    java
    id("net.fabricmc.fabric-loom")
    id("com.teamresourceful.plugins.minecraft")
}

val mcVersion = project.getMcVersion()
val commonSource = project(":common").sourceSets.main.get()

sourceSets.main.get().java.source(commonSource.java)
sourceSets.main.get().resources.source(commonSource.resources)

loom {
    runs {
        named("client") {
            client()
            configName = "Minecraft Client - Fabric"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Minecraft Server - Fabric"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks.compileJava {
    source(commonSource.allSource)
}

dependencies {
    compileOnly(project(":common"))
    "minecraft"("com.mojang:minecraft:${mcVersion}")

    project.getDependencyVersion(
        "fabric.loader",
        { "implementation"(it) },
        { "implementation"("net.fabricmc:fabric-loader:${it ?: "latest.release"}") }
    )

    project.getDependencyVersion(
        "fabric.api",
        { "api"(it) },
        {
            val version = it ?: throw Exceptions.missingVersion("fabric-api")
            val fullVersion = if (version.contains('+')) version else "$version+${mcVersion}"
            "api"("net.fabricmc.fabric-api:fabric-api:$fullVersion")
        }
    )
}