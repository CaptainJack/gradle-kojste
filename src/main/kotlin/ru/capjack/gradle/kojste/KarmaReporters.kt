package ru.capjack.gradle.kojste

import ru.capjack.gradle.kojste.Config.NPM_KARMA_COVERAGE
import ru.capjack.gradle.kojste.Config.NPM_KARMA_GROWL_REPORTER
import ru.capjack.gradle.kojste.Config.NPM_KARMA_JUNIT_REPORTER
import ru.capjack.gradle.kojste.Config.NPM_KARMA_MOCHA_REPORTER
import ru.capjack.gradle.kojste.Config.NPM_KARMA_TEAMCITY_REPORTER

object KarmaReporters {
	val PROGRESS = KarmaPlugin("progress")
	val MOCHA = KarmaPlugin("mocha", NPM_KARMA_MOCHA_REPORTER)
	val GROWL = KarmaPlugin("growl", NPM_KARMA_GROWL_REPORTER)
	val JUNIT = KarmaPlugin("junit", NPM_KARMA_JUNIT_REPORTER)
	val TEAMCITY = KarmaPlugin("teamcity", NPM_KARMA_TEAMCITY_REPORTER)
	val COVERAGE = KarmaPlugin("coverage", NPM_KARMA_COVERAGE)
}