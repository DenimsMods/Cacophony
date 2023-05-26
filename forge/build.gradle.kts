plugins { id("com.github.johnrengelman.shadow") version "7.1.2" }

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

sourceSets { main { resources { srcDir("src/generated/resources") } } }

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentForge: Configuration = configurations.getByName("developmentForge")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

dependencies {
    forge(libs.forge.api)

    forgeRuntimeLibrary(libs.discord.jda)
    forgeRuntimeLibrary(libs.discord.webhooks)

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionForge")) { isTransitive = false }
}

components.getByName<AdhocComponentWithVariants>("java")
    .withVariantsFromConfiguration(configurations["sourcesElements"]) { skip() }

tasks {
    processResources {
        inputs.property("version", project.version)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        filesMatching("META-INF/mods.toml") { expand("version" to project.version) }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("forge")
    }

    jar { archiveClassifier.set("dev") }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}