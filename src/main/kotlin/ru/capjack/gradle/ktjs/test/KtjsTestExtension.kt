package ru.capjack.gradle.ktjs.test

import ru.capjack.gradle.ktjs.test.karma.KarmaBrowser
import ru.capjack.gradle.ktjs.test.karma.KarmaFramework
import ru.capjack.gradle.ktjs.test.karma.KarmaReporter

interface KtjsTestExtension {
	var includeSourceMaps: Boolean
	
	var nodeDependencies: List<NpmPackage>
	
	var karmaFrameworks: List<KarmaFramework>
	var karmaBrowsers: List<KarmaBrowser>
	var karmaReporters: List<KarmaReporter>
	
	var karmaProperties: MutableMap<String, Any>
	
	fun nodeDependencies(vararg values: NpmPackage)
	
	fun karmaFrameworks(vararg values: KarmaFramework)
	fun karmaBrowsers(vararg values: KarmaBrowser)
	fun karmaReporters(vararg values: KarmaReporter)
}