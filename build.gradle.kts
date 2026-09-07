plugins {
    id("java-library")
    id("xyz.jpenilla.run-velocity") version "3.1.0"
    id("com.gradleup.shadow") version "9.6.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:4.1.1-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:4.1.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:4.1.2")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        relocate("org.spongepowered.configurate", "win.stvworlds.stvvelocity.libs.configurate")
        relocate("io.leangen.geantyref", "win.stvworlds.stvvelocity.libs.geantyref")
        relocate("com.typesafe.config", "win.stvworlds.stvvelocity.libs.typesafe")
    }

    runVelocity {
        // Configure the Velocity version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        velocityVersion("4.1.1-SNAPSHOT")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("velocity-plugin.json") {
            expand(props)
        }
    }
}
