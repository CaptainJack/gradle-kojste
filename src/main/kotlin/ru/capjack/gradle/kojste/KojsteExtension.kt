package ru.capjack.gradle.kojste

interface KojsteExtension {
	var includeSourceMaps: Boolean
	
	var nodeDependencies: MutableList<NpmPackage>
	
	var karmaFrameworks: MutableList<KarmaPlugin>
	var karmaBrowsers: MutableList<KarmaPlugin>
	var karmaReporters: MutableList<KarmaPlugin>
	
	var karmaProperties: MutableMap<String, Any>
	
	fun nodeDependencies(vararg values: NpmPackage)
	
	fun karmaFrameworks(vararg values: KarmaPlugin)
	fun karmaBrowsers(vararg values: KarmaPlugin)
	fun karmaReporters(vararg values: KarmaPlugin)
}