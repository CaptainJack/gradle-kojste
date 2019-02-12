package ru.capjack.gradle.ktjs.test

import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_CHROME_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_FIREFOX_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_IE_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_OPERA_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_PHANTOMJS_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA_SAFARI_LAUNCHER
import ru.capjack.gradle.ktjs.test.Config.NPM_PHANTOMJS

object KarmaBrowsers {
	val CHROME = KarmaPlugin("Chrome", NPM_KARMA_CHROME_LAUNCHER)
	val CHROME_CANARY = KarmaPlugin("ChromeCanary", NPM_KARMA_CHROME_LAUNCHER)
	val PHANTOMJS = KarmaPlugin("PhantomJS", NPM_KARMA_PHANTOMJS_LAUNCHER, NPM_PHANTOMJS)
	val FIREFOX = KarmaPlugin("Firefox", NPM_KARMA_FIREFOX_LAUNCHER)
	val OPERA = KarmaPlugin("Opera", NPM_KARMA_OPERA_LAUNCHER)
	val IE = KarmaPlugin("IE", NPM_KARMA_IE_LAUNCHER)
	val SAFARI = KarmaPlugin("Safari", NPM_KARMA_SAFARI_LAUNCHER)
}


