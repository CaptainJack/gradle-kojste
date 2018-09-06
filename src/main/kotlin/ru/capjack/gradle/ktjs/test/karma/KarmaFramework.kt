package ru.capjack.gradle.ktjs.test.karma

import ru.capjack.gradle.ktjs.test.Config.NPM_JASMINE_CORE
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_JASMINE
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_MOCHA
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_QUNIT
import ru.capjack.gradle.ktjs.test.Config.NPM_MOCHA
import ru.capjack.gradle.ktjs.test.Config.NPM_QUNIT
import ru.capjack.gradle.ktjs.test.NpmPackage

enum class KarmaFramework(
	override val pluginName: String,
	override val pluginDependencies: Collection<NpmPackage>
) : KarmaPlugin {
	JASMINE(
		"jasmine",
		listOf(NPM_KARMA_JASMINE, NPM_JASMINE_CORE)
	),
	MOCHA(
		"mocha",
		listOf(NPM_KARMA_MOCHA, NPM_MOCHA)
	),
	QUNIT(
		"qunit",
		listOf(NPM_KARMA_QUNIT, NPM_QUNIT)
	)
}

