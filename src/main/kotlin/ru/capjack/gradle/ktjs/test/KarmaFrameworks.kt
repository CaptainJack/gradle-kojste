package ru.capjack.gradle.ktjs.test

import ru.capjack.gradle.ktjs.test.Config.NPM_JASMINE_CORE
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_JASMINE
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_MOCHA
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_QUNIT
import ru.capjack.gradle.ktjs.test.Config.NPM_MOCHA
import ru.capjack.gradle.ktjs.test.Config.NPM_QUNIT

object KarmaFrameworks {
	val JASMINE = KarmaPlugin("jasmine", NPM_KARMA_JASMINE, NPM_JASMINE_CORE)
	val MOCHA = KarmaPlugin("mocha", NPM_KARMA_MOCHA, NPM_MOCHA)
	val QUNIT = KarmaPlugin("qunit", NPM_KARMA_QUNIT, NPM_QUNIT)
}

