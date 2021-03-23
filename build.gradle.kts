import java.nio.file.Path
import java.nio.file.Paths

plugins {
    idea

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    `kotlin-dsl`

    `maven-publish`

    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`

    id("pt.branden.brandenportal.greeting") version "0.1.0"
}

val repoPath: Path = Paths.get(project.projectDir.absolutePath).resolve("local-plugin-repository")
with(File(repoPath.toUri())) {
    if (!exists()) {
        mkdir()
    }
}

val rootProjectGroup = group

idea {
    module {
        sourceDirs = setOf()
        resourceDirs = setOf()
        testSourceDirs = setOf()
        testResourceDirs = setOf()
    }
}

subprojects {
    apply(plugin = "org.gradle.kotlin.kotlin-dsl")
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "java-gradle-plugin")

    group = rootProjectGroup

    kotlinDslPluginOptions {
        experimentalWarning.set(false)
    }
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.spring.io/milestone")
        }
        maven {
            url = uri("https://plugins.gradle.org/m2")
        }
        maven {
            url = uri("https://dl.bintray.com/konform-kt/konform")
        }
        maven {
            name = "localPlugin"
            url = uri(repoPath.toUri())
        }
        jcenter()
        google()
    }

    project.task<Delete>("cleanLocalRepo") {
        delete(repoPath)
    }

    tasks.clean {
        dependsOn(tasks["cleanLocalRepo"])
    }
}
