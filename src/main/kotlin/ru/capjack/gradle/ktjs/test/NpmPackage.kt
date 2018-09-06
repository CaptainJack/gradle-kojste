package ru.capjack.gradle.ktjs.test

class NpmPackage(
	val name: String,
	val version: String
) {
	override fun equals(other: Any?): Boolean {
		return this === other || name == (other as? NpmPackage)?.name
	}
	
	override fun hashCode(): Int {
		return name.hashCode()
	}
	
	override fun toString(): String {
		return "$name@$version"
	}
}
