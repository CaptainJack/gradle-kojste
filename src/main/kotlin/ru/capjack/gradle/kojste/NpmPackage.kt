package ru.capjack.gradle.kojste

data class NpmPackage(
	val name: String,
	val version: String
) {
	override fun toString(): String {
		return "$name@$version"
	}
}
