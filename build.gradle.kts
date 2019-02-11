plugins {
	`kotlin-dsl`
	`java-gradle-plugin`
	`maven-publish`
	id("com.gradle.plugin-publish") version "0.10.0"
	id("nebula.release") version "9.2.0"
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
	plugins.create("KtjsTest") {
		id = "ru.capjack.ktjs-test"
		implementationClass = "ru.capjack.gradle.ktjs.test.KtjsTestPlugin"
		displayName = "KtjsTest"
	}
}

pluginBundle {
	vcsUrl = "https://github.com/CaptainJack/gradle-ktjs-test"
	website = vcsUrl
	description = "Provides test runtime for Kotlin JavaScript on Node.js and Karma"
	tags = listOf("kotlin", "javascript", "test")
}

tasks["postRelease"].dependsOn("publishPlugins")