plugins { id("com.github.johnrengelman.shadow") version "7.1.2" }

architectury {
    platformSetupLoomIde()
    fabric()
}

loom { accessWidenerPath.set(project(":common").loom.accessWidenerPath) }

/**
 * @see: https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html
 * */
val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentFabric: Configuration = configurations.getByName("developmentFabric")
configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

repositories {
    maven {
        name = "TerraformersMC - ModMenu"
        url = uri("https://maven.terraformersmc.com/releases/")
        content { includeGroup("com.terraformersmc") }
    }
}

dependencies {
    modApi(libs.fabric.api)
    modImplementation(libs.fabric.loader)

    modLocalRuntime("com.terraformersmc:modmenu:3.2.5")

    localRuntime(libs.discord.jda)
    localRuntime(libs.discord.webhooks)

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }
}

components.getByName<AdhocComponentWithVariants>("java")
    .withVariantsFromConfiguration(configurations["sourcesElements"]) { skip() }

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") { expand("version" to project.version) }
    }

    shadowJar {
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("fabric")
    }

    jar { archiveClassifier.set("dev") }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}
