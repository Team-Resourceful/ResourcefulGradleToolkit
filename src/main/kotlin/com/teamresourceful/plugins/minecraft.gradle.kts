package com.teamresourceful.plugins

import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.*

plugins {
    java
}

base {
    archivesName = rootProject.base.archivesName
}

repositories {
    mavenCentral()
    maven("https://maven.teamresourceful.com/repository/maven-public")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.fabricmc.net/")
    maven("https://repo.spongepowered.org/repository/maven-public")
    maven("https://maven.parchmentmc.org")
}

java {
    withSourcesJar()
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    inputs.property("version", project.version)
    filesMatching(listOf("META-INF/neoforge.mods.toml", "fabric.mod.json")) {
        expand("version" to project.version)
    }
}