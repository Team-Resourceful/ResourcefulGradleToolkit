package com.teamresourceful.plugins

import com.teamresourceful.utils.getMcVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.java

plugins {
    java
    id("architectury-plugin")
}

architectury {
    minecraft = project.getMcVersion()
}

subprojects {
    apply(plugin = "com.teamresourceful.plugins.loom-platform")
}