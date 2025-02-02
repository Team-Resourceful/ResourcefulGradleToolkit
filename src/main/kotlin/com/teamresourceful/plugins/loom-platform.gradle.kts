package com.teamresourceful.plugins

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.teamresourceful.utils.*
import com.teamresourceful.utils.Platform
import dev.architectury.plugin.ArchitectPluginExtension
import gradle.kotlin.dsl.accessors._089421750eab8b3b217fdee3b8ddbbf9.*
import gradle.kotlin.dsl.accessors._089421750eab8b3b217fdee3b8ddbbf9.base
import gradle.kotlin.dsl.accessors._089421750eab8b3b217fdee3b8ddbbf9.jar
import gradle.kotlin.dsl.accessors._089421750eab8b3b217fdee3b8ddbbf9.java
import gradle.kotlin.dsl.accessors._089421750eab8b3b217fdee3b8ddbbf9.processResources
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.*

plugins {
    java
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") apply false
}

val platform = project.getPlatform()
val mcVersion = project.getMcVersion()

base {
    archivesName = rootProject.base.archivesName
}

configure<LoomGradleExtensionAPI> {
    silentMojangMappingsLicense()
}

repositories {
    maven("https://maven.teamresourceful.com/repository/maven-public")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
}

dependencies {
    "minecraft"("com.mojang:minecraft:$mcVersion")

    "mappings"(project.the<LoomGradleExtensionAPI>().layered {
        officialMojangMappings()
        rootProject.getParchmentMavenCoordinate()?.let(::parchment)
    })
}

java {
    withSourcesJar()
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier.set(null as String?)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching(listOf("META-INF/neoforge.mods.toml", "fabric.mod.json")) {
        expand("version" to project.version)
    }
}

when (platform) {
    Platform.COMMON -> {

        val shade by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        configure<ArchitectPluginExtension> {
            val enabledPlatforms: String by rootProject
            common(enabledPlatforms.split(","))
        }

        tasks {
            "jar"(Jar::class) {
                manifest {
                    attributes["Fabric-Loom-Remap"] = true
                }
            }

            "shadowJar"(ShadowJar::class) {
                archiveClassifier.set(null)

                configurations = listOf(shade)
            }
        }
    }
    else -> {
        apply(plugin = "com.github.johnrengelman.shadow")

        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
            loader(platform.modLoader!!)
        }

        val common: Configuration by configurations.creating {
            configurations.compileClasspath.get().extendsFrom(this)
            configurations.runtimeClasspath.get().extendsFrom(this)
            configurations["development${platform.archDevName}"].extendsFrom(this)
        }

        val shadowCommon by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        dependencies {
            common(project(":common", configuration = "namedElements")) { isTransitive = false }
            shadowCommon(project(path = ":common", configuration = "transformProduction${platform.archDevName}")) { isTransitive = false }

            when {
                platform == Platform.FABRIC -> {
                    project.getDependencyVersion("fabric.loader", ::modImplementation) {
                        modImplementation(group = "net.fabricmc", name = "fabric-loader", version = it ?: "latest.release")
                    }
                    project.getDependencyVersion("fabric.api", ::modApi) {
                        val version = it ?: throw Exceptions.missingVersion("fabric-api")
                        modApi(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "$version+${mcVersion}")
                    }
                }
                platform == Platform.NEOFORGE -> {
                    project.getDependencyVersion("neoforge", { "neoForge"(it) }) {
                        val version = it ?: throw Exceptions.missingVersion("neoforge")
                        "neoForge"(group = "net.neoforged", name = "neoforge", version = version)
                    }
                }
            }
        }

        tasks {

            "shadowJar"(ShadowJar::class) {
                exclude("architectury.common.json")

                configurations = listOf(shadowCommon)
                archiveAppendix.set("${platform.id}-$mcVersion")
                archiveClassifier.set("dev-shadow")
            }

            "remapJar"(RemapJarTask::class) {
                val task = named<Jar>("shadowJar").get()

                inputFile.set(task.archiveFile)
                dependsOn(task)
                archiveAppendix.set("${platform.id}-$mcVersion")
                archiveClassifier.set(null)
                injectAccessWidener.set(true)
            }

            "jar"(Jar::class) {
                archiveAppendix.set("${platform.id}-$mcVersion")
                archiveClassifier.set("dev")
            }

            "sourcesJar"(Jar::class) {
                val task = project(":common").tasks.named<Jar>("sourcesJar").get()
                dependsOn(task)
                from(task.archiveFile.map(project::zipTree))
            }
        }

        components.getByName<SoftwareComponent>("java") {
            (this as AdhocComponentWithVariants).apply {
                withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
                    skip()
                }
            }
        }
    }
}