package com.teamresourceful.plugins

import com.teamresourceful.utils.*
import org.gradle.kotlin.dsl.*

plugins {
    java
    id("net.neoforged.moddev")
    id("com.teamresourceful.plugins.minecraft")
}

val mcVersion = project.getMcVersion()
val commonSource = project(":common").sourceSets.main.get()

sourceSets.main.get().java.source(commonSource.java)
sourceSets.main.get().resources.source(commonSource.resources)

neoForge {
    version = project.getDependencyVersion("neoforge") ?: throw Exceptions.missingVersion("neoforge")

    runs {
        create("client") {
            client()
            ideName = "Minecraft Client - NeoForge"
        }

        create("server") {
            server()
            ideName = "Minecraft Server - NeoForge"
        }
    }

    mods.create(project.name).sourceSet(project.sourceSets.getByName("main"))
}

tasks.compileJava {
    source(commonSource.allSource)
}

dependencies {
    compileOnly(project(":common"))
}