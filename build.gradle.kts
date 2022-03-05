import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    kotlin("jvm").version(System.getProperty("kotlinVersion"))
}

base {
    archivesName.set("${project.property("archivesBaseName")}")
}

version = project.property("modVersion") as String
group = project.property("mavenGroup") as String

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraftVersion")}")
    mappings("net.fabricmc:yarn:${project.property("yarnMappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabricVersion")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabricKotlinVersion")}")
}

tasks {
    val javaVersion = JavaVersion.VERSION_16

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        if (JavaVersion.current().isJava9Compatible) {
            options.release.set(javaVersion.toString().toInt())
        }
    }

    withType<KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName}" }
        }
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
