package ru.capjack.gradle.kojste

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import groovy.json.JsonBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.task
import org.gradle.process.ExecSpec
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleJavaTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import ru.capjack.gradle.kojste.Config.EXTENSION
import ru.capjack.gradle.kojste.Config.NODE_DIR
import ru.capjack.gradle.kojste.Config.NODE_VERSION
import ru.capjack.gradle.kojste.Config.NPM_KARMA
import ru.capjack.gradle.kojste.Config.TASK_COPY_DEPENDENCIES
import ru.capjack.gradle.kojste.Config.TASK_INIT_KARMA
import ru.capjack.gradle.kojste.Config.TASK_INIT_NPM
import ru.capjack.gradle.kojste.Config.TASK_RUN
import ru.capjack.gradle.kojste.Config.WORK_DIR
import java.io.File
import java.io.OutputStream

class KojstePlugin : Plugin<Project> {
	override fun apply(project: Project) {
		configureNode(project)
		
		project.extensions.create(KojsteExtension::class, EXTENSION, KojsteExtensionImpl::class)
		
		project.afterEvaluate {
			createTaskInitNpm(this)
			
			
			val extension = extensions.getByType<KotlinProjectExtension>()
			if (extension is KotlinMultiplatformExtension) {
				extension.targets.forEach(::processTarget)
			}
			else if (extension is KotlinSingleJavaTargetExtension) {
				processTarget(extension.target)
			}
		}
	}
	
	private fun configureNode(project: Project) {
		project.pluginManager.apply(NodePlugin::class)
		
		val dir = project.kojsteNodeDir
		
		project.configure<NodeExtension> {
			download = true
			version = NODE_VERSION
			workDir = dir.resolve("node")
			nodeModulesDir = dir
		}
	}
	
	private fun createTaskInitNpm(project: Project) {
		val ext = project.kojsteExtensions
		
		project.task<NpmTask>(TASK_INIT_NPM) {
			val dependencies = listOf(NPM_KARMA)
				.plus(ext.karmaFrameworks.flatMap(KarmaPlugin::dependencies))
				.plus(ext.karmaBrowsers.flatMap(KarmaPlugin::dependencies))
				.plus(ext.karmaReporters.flatMap(KarmaPlugin::dependencies))
				.plus(ext.nodeDependencies)
			
			inputs.property("dependencies", dependencies.joinToString())
			outputs.file(project.kojsteNodeDir.resolve("package-lock.json"))
			
			setArgs(listOf("install", "--silent") + dependencies.map { it.toString() })
			
			setExecOverrides(closureOf<ExecSpec> {
				standardOutput = object : OutputStream() {
					override fun write(b: Int) {}
				}
			})
		}
	}
	
	private fun processTarget(target: KotlinTarget) {
		if (target.platformType == KotlinPlatformType.js) {
			
			val settings = TargetSettings.factory(
				target.project,
				target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME),
				target.compilations.getByName(KotlinCompilation.TEST_COMPILATION_NAME) as KotlinCompilationToRunnableFiles
			)
			
			createTargetTaskCopyDependencies(settings)
			createTargetTaskInitKarma(settings)
			createTargetTaskRun(settings)
		}
	}
	
	private fun createTargetTaskCopyDependencies(settings: TargetSettings) {
		settings.project.task<Copy>(settings.copyDependenciesTaskName) {
			project.afterEvaluate {
				
				into(settings.dependenciesDir)
				configurations[settings.compilationTest.runtimeDependencyConfigurationName].forEach { file ->
					
					from(zipTree(file).matching {
						include("*.js")
						exclude("*.meta.js")
						if (kojsteExtensions.includeSourceMaps) {
							include("*.js.map")
						}
					})
					
				}
				
			}
		}
	}
	
	private fun createTargetTaskInitKarma(settings: TargetSettings) {
		settings.project.tasks.create(settings.initKarmaTaskName) {
			project.afterEvaluate {
				dependsOn(settings.copyDependenciesTaskName)
				
				
				val ext = kojsteExtensions
				val outFiles = files(
					tasks.getByName<Kotlin2JsCompile>(settings.compilationMain.compileKotlinTaskName).outputFile,
					tasks.getByName<Kotlin2JsCompile>(settings.compilationTest.compileKotlinTaskName).outputFile
				)
				
				inputs.files(outFiles)
				inputs.property(
					"karma",
					listOf<String>()
						.plus("includeSourceMaps:" + ext.includeSourceMaps)
						.plus(ext.karmaFrameworks.map(KarmaPlugin::name))
						.plus(ext.karmaBrowsers.map(KarmaPlugin::name))
						.plus(ext.karmaReporters.map(KarmaPlugin::name))
						.joinToString()
						.plus(ext.karmaProperties.map { "${it.key}:${it.value}" }.joinToString())
				)
				outputs.file(settings.karmaFile)
				
				doLast {
					
					val files = LinkedHashSet<String>()
					files.add("kotlin.js")
					files.addAll(settings.dependenciesDir.listFiles().map { it.name })
					files.addAll(outFiles.map { it.absolutePath })
					
					if (ext.includeSourceMaps) {
						files.addAll(outFiles.map { it.absolutePath + ".map" })
					}
					
					val properties = mutableMapOf(
						"basePath" to settings.dependenciesDir.absolutePath,
						"files" to files,
						"browsers" to ext.karmaBrowsers.map { it.name },
						"frameworks" to ext.karmaFrameworks.map { it.name },
						"reporters" to ext.karmaReporters.map { it.name }
					)
					properties.putAll(ext.karmaProperties)
					
					settings.karmaFile.writeText(
						"module.exports = function(config) { config.set(${JsonBuilder(properties).toPrettyString()}) };"
					)
				}
			}
		}
	}
	
	private fun createTargetTaskRun(settings: TargetSettings) {
		settings.project.task<NodeTask>(settings.runTaskName) {
			dependsOn(TASK_INIT_NPM, settings.initKarmaTaskName, settings.copyDependenciesTaskName)
			
			setScript(project.kojsteNodeDir.resolve("node_modules/karma/bin/karma"))
			setArgs(listOf("start", settings.karmaFile.absolutePath))
			
			setIgnoreExitValue(true)
			
			doLast {
				if (result.exitValue != 0) {
					throw TaskExecutionException(this, Exception("Kotlin JS tests fail"))
				}
			}
		}
		
		settings.project.tasks[settings.testTaskName].dependsOn(settings.runTaskName)
	}
	
	private val Project.kojsteNodeDir: File
		get() = rootProject.buildDir.resolve(NODE_DIR)
	
	private val Project.kojsteExtensions: KojsteExtension
		get() = project.extensions.getByName<KojsteExtension>(EXTENSION)
	
	internal class TargetSettings(
		val project: Project,
		val compilationMain: KotlinCompilation<*>,
		val compilationTest: KotlinCompilationToRunnableFiles<*>,
		val copyDependenciesTaskName: String,
		val initKarmaTaskName: String,
		val runTaskName: String,
		val testTaskName: String,
		val dependenciesDir: File,
		val karmaFile: File
	) {
		
		companion object {
			fun factory(project: Project, compilationMain: KotlinCompilation<*>, compilationTest: KotlinCompilationToRunnableFiles<*>): TargetSettings {
				val name = compilationMain.target.name
				val single = name == "2Js"
				
				return if (name == "2Js") {
					val workDir = project.buildDir.resolve(WORK_DIR)
					
					TargetSettings(
						project, compilationMain, compilationTest,
						TASK_COPY_DEPENDENCIES,
						TASK_INIT_KARMA,
						TASK_RUN,
						"test",
						workDir.resolve("dependencies"),
						workDir.resolve("karma.js")
					)
				}
				else {
					val taskSuffix = if (single) "" else name.capitalize()
					val workDir = project.buildDir.resolve(WORK_DIR).resolve(name)
					
					TargetSettings(
						project, compilationMain, compilationTest,
						TASK_COPY_DEPENDENCIES + taskSuffix,
						TASK_INIT_KARMA + taskSuffix,
						TASK_RUN + taskSuffix,
						name + "Test",
						workDir.resolve("dependencies"),
						workDir.resolve("karma.js")
					)
				}
			}
		}
	}
}