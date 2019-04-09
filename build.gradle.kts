plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	`maven-publish`
	id("com.gradle.plugin-publish") version "0.10.1"
	id("nebula.release") version "10.0.1"
}

group = "ru.capjack.gradle"

repositories {
	jcenter()
	gradlePluginPortal()
}

dependencies {
	implementation("com.moowork.gradle:gradle-node-plugin:1.2.0")
	implementation(kotlin("gradle-plugin"))
}

gradlePlugin {
	plugins.create("Kojste") {
		id = "ru.capjack.kojste"
		implementationClass = "ru.capjack.gradle.kojste.KojstePlugin"
		displayName = "Kojste"
	}
}

pluginBundle {
	vcsUrl = "https://github.com/CaptainJack/gradle-ktjste"
	website = vcsUrl
	description = "Provides test runtime for Kotlin JavaScript on Node.js and Karma"
	tags = listOf("kotlin", "javascript", "test")
}

tasks["postRelease"].dependsOn("publishPlugins")