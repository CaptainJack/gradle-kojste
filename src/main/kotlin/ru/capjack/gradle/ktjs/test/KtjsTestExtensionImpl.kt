package ru.capjack.gradle.ktjs.test

import ru.capjack.gradle.ktjs.test.karma.KarmaBrowser
import ru.capjack.gradle.ktjs.test.karma.KarmaFramework
import ru.capjack.gradle.ktjs.test.karma.KarmaReporter

internal open class KtjsTestExtensionImpl : KtjsTestExtension {
	
	override var nodeDependencies = emptyList<NpmPackage>()
	
	override var karmaFrameworks = listOf(KarmaFramework.MOCHA)
	override var karmaBrowsers = listOf(KarmaBrowser.PHANTOMJS)
	override var karmaReporters = listOf<KarmaReporter>()
	
	override var karmaProperties =  mutableMapOf<String, Any>()
	
	override fun nodeDependencies(vararg values: NpmPackage) {
		nodeDependencies = values.toList()
	}
	
	override fun karmaFrameworks(vararg values: KarmaFramework) {
		karmaFrameworks = values.toList()
	}
	
	override fun karmaBrowsers(vararg values: KarmaBrowser) {
		karmaBrowsers = values.toList()
	}
	
	override fun karmaReporters(vararg values: KarmaReporter) {
		karmaReporters = values.toList()
	}
}