package ru.capjack.gradle.ktjs.test

data class NpmPackage(
	val name: String,
	val version: String
) {
	override fun toString(): String {
		return "$name@$version"
	}
}
