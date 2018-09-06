package ru.capjack.gradle.ktjs.test.karma

import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_CHROME_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_FIREFOX_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_IE_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_OPERA_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_PHANTOMJS_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_SAFARI_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_PHANTOMJS
import ru.capjack.gradle.ktjs.test.NpmPackage

enum class KarmaBrowser(
	override val pluginName: String,
	override val pluginDependencies: Collection<NpmPackage>
) : KarmaPlugin {
	
	CHROME(
		"Chrome",
		listOf(NPM_KARMA_CHROME_LAUNCHER)
	),
	CHROME_CANARY(
		"ChromeCanary",
		listOf(NPM_KARMA_CHROME_LAUNCHER)
	),
	PHANTOMJS(
		"PhantomJS",
		listOf(NPM_KARMA_PHANTOMJS_LAUNCHER, NPM_PHANTOMJS)
	),
	FIREFOX(
		"Firefox",
		listOf(NPM_KARMA_FIREFOX_LAUNCHER)
	),
	OPERA(
		"Opera",
		listOf(NPM_KARMA_OPERA_LAUNCHER)
	),
	IE(
		"IE",
		listOf(NPM_KARMA_IE_LAUNCHER)
	),
	SAFARI(
		"Safari",
		listOf(NPM_KARMA_SAFARI_LAUNCHER)
	)
}


