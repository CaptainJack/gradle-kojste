package ru.capjack.gradle.kojste

internal open class KojsteExtensionImpl : KojsteExtension {
	override var includeSourceMaps: Boolean = false
	
	override var nodeDependencies = mutableListOf<NpmPackage>()
	
	override var karmaFrameworks = mutableListOf(KarmaFrameworks.MOCHA)
	override var karmaBrowsers = mutableListOf(KarmaBrowsers.PHANTOMJS)
	override var karmaReporters = mutableListOf(KarmaReporters.MOCHA)
	
	override var karmaProperties = mutableMapOf<String, Any>(
		"singleRun" to true,
		"autoWatch" to false,
		"failOnEmptyTestSuite" to false,
		"colors" to false,
		"mochaReporter" to mapOf("output" to "minimal")
	)
	
	override fun nodeDependencies(vararg values: NpmPackage) {
		nodeDependencies = values.toMutableList()
	}
	
	override fun karmaFrameworks(vararg values: KarmaPlugin) {
		karmaFrameworks = values.toMutableList()
	}
	
	override fun karmaBrowsers(vararg values: KarmaPlugin) {
		karmaBrowsers = values.toMutableList()
	}
	
	override fun karmaReporters(vararg values: KarmaPlugin) {
		karmaReporters = values.toMutableList()
	}
}