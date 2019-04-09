package ru.capjack.gradle.kojste

data class KarmaPlugin(
	val name: String,
	val dependencies: Collection<NpmPackage> = emptyList()
) {
	constructor(name: String, vararg dependencies: NpmPackage) : this(name, dependencies.toList())
}

