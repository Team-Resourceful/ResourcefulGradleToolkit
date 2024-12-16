package com.teamresourceful.plugins

import com.teamresourceful.utils.getMcVersion
import gradle.kotlin.dsl.accessors._3b58a1cd1deee8d68d3caeaf5b5059ff.architectury
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