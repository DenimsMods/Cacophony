architectury { common(property("enabled_platforms").toString().split(",")) }

loom { accessWidenerPath.set(file("src/main/resources/cacophony.accesswidener")) }

// We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
dependencies {
    modImplementation(libs.fabric.loader)

    implementation(libs.discord.jda) {
        exclude(module = "opus-java")
    }
    implementation(libs.discord.webhooks)
}

tasks.withType<Jar> { from(rootProject.files("COPYING", "COPYING.LESSER")) }
