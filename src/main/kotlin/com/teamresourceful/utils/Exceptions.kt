package com.teamresourceful.utils

import org.gradle.api.GradleException

internal object Exceptions {

    fun missingVersion(field: String) =  GradleException("Missing version '$field' in libs.versions.toml")
}