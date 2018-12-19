package ru.capjack.gradle.ktjs.test

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import groovy.json.JsonBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.task
import org.gradle.kotlin.dsl.withType
import org.gradle.process.ExecSpec
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import ru.capjack.gradle.ktjs.test.Config.EXTENSION
import ru.capjack.gradle.ktjs.test.Config.NODE_DIR
import ru.capjack.gradle.ktjs.test.Config.NODE_VERSION
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA
import ru.capjack.gradle.ktjs.test.Config.TASK_COPY_DEPENDENCIES
import ru.capjack.gradle.ktjs.test.Config.TASK_INIT_KARMA
import ru.capjack.gradle.ktjs.test.Config.TASK_INIT_NPM
import ru.capjack.gradle.ktjs.test.Config.TASK_RUN
import ru.capjack.gradle.ktjs.test.Config.WORK_DIR
import ru.capjack.gradle.ktjs.test.karma.KarmaPlugin
import java.io.File
import java.io.OutputStream

class KtjsTestPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply(NodePlugin::class)
		
		val nodeDir = project.rootProject.buildDir.resolve(NODE_DIR)
		
		project.configure<NodeExtension> {
			download = true
			version = NODE_VERSION
			workDir = nodeDir.resolve("node")
			nodeModulesDir = nodeDir
		}
		
		val ext = project.extensions.create(KtjsTestExtension::class, EXTENSION, KtjsTestExtensionImpl::class)
		
		project.afterEvaluate {
			
			project.task<NpmTask>(TASK_INIT_NPM) {
				val dependencies = listOf(NPM_KARMA)
					.plus(ext.karmaFrameworks.flatMap(KarmaPlugin::pluginDependencies))
					.plus(ext.karmaBrowsers.flatMap(KarmaPlugin::pluginDependencies))
					.plus(ext.karmaReporters.flatMap(KarmaPlugin::pluginDependencies))
					.plus(ext.nodeDependencies)
				
				inputs.property("dependencies", dependencies.joinToString())
				outputs.file(nodeDir.resolve("package-lock.json"))
				
				setArgs(listOf("install", "--silent") + dependencies.map { it.toString() })
				
				setExecOverrides(closureOf<ExecSpec> {
					standardOutput = object : OutputStream() {
						override fun write(b: Int) {}
					}
				})
			}
			
			project.tasks.withType<Kotlin2JsCompile>()
				.filter { it.name.startsWith("compileTestKotlin") }
				.forEach { task ->
					
					val runtimeConfigurationName: String
					val copyDependenciesTaskName: String
					val initKarmaTaskName: String
					val runTaskName: String
					val testTaskName: String
					val workDir: File
					
					if (task.name == "compileTestKotlin2Js") {
						copyDependenciesTaskName = TASK_COPY_DEPENDENCIES
						initKarmaTaskName = TASK_INIT_KARMA
						runTaskName = TASK_RUN
						testTaskName = "test"
						runtimeConfigurationName = "testRuntimeClasspath"
						
						workDir = project.buildDir.resolve(WORK_DIR)
					}
					else {
						val nameCapitalized = task.name.substringAfter("compileTestKotlin")
						val name = nameCapitalized.decapitalize()
						
						copyDependenciesTaskName = TASK_COPY_DEPENDENCIES + nameCapitalized
						initKarmaTaskName = TASK_INIT_KARMA + nameCapitalized
						runTaskName = TASK_RUN + nameCapitalized
						testTaskName = name + "Test"
						runtimeConfigurationName = name + "TestRuntimeClasspath"
						
						workDir = project.buildDir.resolve(WORK_DIR).resolve(name)
					}
					
					val runtimeConfiguration = project.configurations[runtimeConfigurationName]
					
					val tasks = listOf(
						project.tasks.getByName<Kotlin2JsCompile>("compile" + task.name.substringAfter("compileTest")),
						task
					)
					
					val karmaFile = workDir.resolve("karma.js")
					val outFiles = tasks.map { it.outputFile }
					
					project.afterEvaluate {
						
						project.task<Copy>(copyDependenciesTaskName) {
							into(workDir.resolve("dependencies"))
							runtimeConfiguration.forEach { file ->
								from(project.zipTree(file).matching {
									include("*.js")
									if (ext.includeSourceMaps) {
										include("*.js.map")
									}
								})
							}
						}
						
						project.task(initKarmaTaskName) {
							dependsOn(copyDependenciesTaskName)
							
							inputs.files(outFiles)
							inputs.property(
								"karma",
								listOf<String>()
									.plus("includeSourceMaps:" + ext.includeSourceMaps)
									.plus(ext.karmaFrameworks.map(KarmaPlugin::pluginName))
									.plus(ext.karmaBrowsers.map(KarmaPlugin::pluginName))
									.plus(ext.karmaReporters.map(KarmaPlugin::pluginName))
									.joinToString()
									.plus(ext.karmaProperties.map { "${it.key}:${it.value}" }.joinToString())
							)
							outputs.file(karmaFile)
							
							doLast {
								
								val files = mutableListOf("kotlin.js", "*.js")
								files.addAll(outFiles.map { it.absolutePath })
								
								if (ext.includeSourceMaps) {
									files.add("*.js.map")
									files.add("kotlin.js.map")
									files.addAll(outFiles.map { it.absolutePath + ".map" })
								}
								
								val properties = mutableMapOf(
									"basePath" to workDir.resolve("dependencies").absolutePath,
									"files" to files,
									"browsers" to ext.karmaBrowsers.map { it.pluginName },
									"frameworks" to ext.karmaFrameworks.map { it.pluginName },
									"reporters" to ext.karmaReporters.map { it.pluginName }.plus("progress"),
									"singleRun" to true,
									"autoWatch" to false
								)
								properties.putAll(ext.karmaProperties)
								
								karmaFile.writeText(
									"module.exports = function(config) { config.set(${JsonBuilder(properties).toPrettyString()}) };"
								)
							}
							
						}
						
						project.task<NodeTask>(runTaskName) {
							dependsOn(TASK_INIT_NPM, initKarmaTaskName, copyDependenciesTaskName)
							
							setScript(nodeDir.resolve("node_modules/karma/bin/karma"))
							setArgs(listOf("start", karmaFile.absolutePath))
						}
						
						project.tasks[testTaskName].dependsOn(runTaskName)
					}
				}
		}
	}
}

/*
private fun init(project: Project) {
	val buildDir = project.buildDir.resolve(BUILD_DIR)
	val workDir = project.extensions.getByType<NodeExtension>().nodeModulesDir
	val nodeModulesDir = workDir.resolve("node_modules")
	val ext = project.extensions.getByType<KtjsTestExtension>()
	
	val dependenciesDir = buildDir.resolve("dependencies")
	val karmaFile = buildDir.resolve("karma-config.js")
	
	project.task<Copy>(TASK_COPY_DEPENDENCIES) {
		into(dependenciesDir)
		project.configurations["testRuntimeClasspath"].forEach {
			from(project.zipTree(it).matching {
				include("*.js")
				if (ext.includeSourceMaps) {
					include("*.js.map")
				}
			})
		}
	}
	
	project.task(TASK_CREATE_KARMA_CONFIG) {
		dependsOn(TASK_COPY_DEPENDENCIES)
		
		inputs.dir(dependenciesDir)
		inputs.files(project.tasks.withType<Kotlin2JsCompile>().map { it.outputFile })
		inputs.property(
			"karma",
			listOf<String>()
				.plus("includeSourceMaps:" + ext.includeSourceMaps)
				.plus(ext.karmaFrameworks.map(KarmaPlugin::pluginName))
				.plus(ext.karmaBrowsers.map(KarmaPlugin::pluginName))
				.plus(ext.karmaReporters.map(KarmaPlugin::pluginName))
				.joinToString()
				.plus(ext.karmaProperties.map { "${it.key}:${it.value}" }.joinToString())
		)
		outputs.file(karmaFile)
		
		doLast {
			val outFiles = project.tasks.withType<Kotlin2JsCompile>().map { it.outputFile }
			
			val files = mutableListOf("kotlin.js", "*.js")
			files.addAll(outFiles.map { it.absolutePath })
			
			if (ext.includeSourceMaps) {
				files.add("*.js.map")
				files.add("kotlin.js.map")
				files.addAll(outFiles.map { it.absolutePath + ".map" })
			}
			
			val properties = mutableMapOf(
				"basePath" to dependenciesDir.absolutePath,
				"files" to files,
				"browsers" to ext.karmaBrowsers.map { it.pluginName },
				"frameworks" to ext.karmaFrameworks.map { it.pluginName },
				"reporters" to ext.karmaReporters.map { it.pluginName }.plus("progress"),
				"singleRun" to true,
				"autoWatch" to false
			)
			properties.putAll(ext.karmaProperties)
			
			karmaFile.writeText(
				"module.exports = function(config) { config.set(${JsonBuilder(properties).toPrettyString()}) };"
			)
		}
	}
	
	
	
	project.task<NodeTask>(TASK_RUN) {
		dependsOn(TASK_CREATE_KARMA_CONFIG, TASK_INSTALL_NPM_DEPENDENCIES)
		
		setScript(nodeModulesDir.resolve("karma/bin/karma"))
		setArgs(listOf("start", karmaFile.absolutePath))
		
	}
	
	project.tasks["test"].dependsOn(TASK_RUN)
}*/
