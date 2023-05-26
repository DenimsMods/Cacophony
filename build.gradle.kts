@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.1-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.8.0" apply false
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "io.github.juuxel.loom-quiltflower")

    val loom = extensions.getByName<LoomGradleExtensionAPI>("loom").apply { silentMojangMappingsLicense() }

    repositories {
        maven {
            name = "ParchmentMC"
            url = uri("https://maven.parchmentmc.net/")
            content { includeGroup("org.parchmentmc.data") }
        }
    }

    dependencies {
        with (rootProject.libs) {
            "minecraft"(minecraft)
            "mappings"(loom.layered {
                officialMojangMappings()
                parchment("${parchment.get()}@zip")
            })
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")

    base.archivesName.set(property("archives_base_name").toString())
    version = property("mod_version").toString()
    group = property("maven_group").toString()

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java { withSourcesJar() }
}
