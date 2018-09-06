package ru.capjack.gradle.ktjs.test.karma

import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_COVERAGE
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_GROWL_REPORTER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_JUNIT_REPORTER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_TEAMCITY_REPORTER
import ru.capjack.gradle.ktjs.test.NpmPackage

enum class KarmaReporter(
	override val pluginName: String,
	override val pluginDependencies: Collection<NpmPackage>
) : KarmaPlugin {
	GROWL(
		"growl",
		listOf(NPM_KARMA_GROWL_REPORTER)
	),
	JUNIT(
		"junit", listOf(NPM_KARMA_JUNIT_REPORTER)
	),
	TEAMCITY(
		"teamcity", listOf(NPM_KARMA_TEAMCITY_REPORTER)
	),
	COVERAGE(
		"coverage", listOf(NPM_KARMA_COVERAGE)
	)
}