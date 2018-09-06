package ru.capjack.gradle.ktjs.test.karma

import ru.capjack.gradle.ktjs.test.NpmPackage

interface KarmaPlugin {
	val pluginName: String
	val pluginDependencies: Collection<NpmPackage>
}