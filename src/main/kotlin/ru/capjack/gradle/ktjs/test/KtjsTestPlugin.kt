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
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.task
import org.gradle.kotlin.dsl.withType
import org.gradle.process.ExecSpec
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import ru.capjack.gradle.ktjs.test.Config.BUILD_DIR
import ru.capjack.gradle.ktjs.test.Config.EXTENSION
import ru.capjack.gradle.ktjs.test.Config.NPM_KARMA
import ru.capjack.gradle.ktjs.test.Config.TASK_COPY_DEPENDENCIES
import ru.capjack.gradle.ktjs.test.Config.TASK_CREATE_KARMA_CONFIG
import ru.capjack.gradle.ktjs.test.Config.TASK_INSTALL_NPM_DEPENDENCIES
import ru.capjack.gradle.ktjs.test.Config.TASK_RUN
import ru.capjack.gradle.ktjs.test.Config.VERSION_NODE
import ru.capjack.gradle.ktjs.test.karma.KarmaPlugin
import java.io.OutputStream

class KtjsTestPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply(NodePlugin::class)
		project.configure<NodeExtension> {
			version = VERSION_NODE
			download = true
			workDir = project.buildDir.resolve("$BUILD_DIR/nodejs")
			nodeModulesDir = project.buildDir.resolve("$BUILD_DIR/work")
		}
		
		project.extensions.create(KtjsTestExtension::class, EXTENSION, KtjsTestExtensionImpl::class)
		
		project.afterEvaluate(::init)
	}
	
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
				from(project.zipTree(it).matching { include("*.js", "*.js.map") })
			}
		}
		
		project.task(TASK_CREATE_KARMA_CONFIG) {
			dependsOn(TASK_COPY_DEPENDENCIES)
			
			inputs.dir(dependenciesDir)
			inputs.files(project.tasks.withType<Kotlin2JsCompile>().map { it.outputFile })
			inputs.property(
				"karma",
				listOf<String>()
					.plus(ext.karmaFrameworks.map(KarmaPlugin::pluginName))
					.plus(ext.karmaBrowsers.map(KarmaPlugin::pluginName))
					.plus(ext.karmaReporters.map(KarmaPlugin::pluginName))
					.joinToString()
					.plus(ext.karmaProperties.map { "${it.key}:${it.value}" }.joinToString())
			)
			outputs.file(karmaFile)
			
			doLast {
				val outFiles = project.tasks.withType<Kotlin2JsCompile>().map { it.outputFile }
				
				val properties = mutableMapOf(
					"basePath" to dependenciesDir.absolutePath,
					"files" to listOf("kotlin.js", "kotlin.js.map", "*.js", "*.js.map") + outFiles.map { it.absolutePath } + outFiles.map { it.absolutePath + ".map" },
					"browsers" to ext.karmaBrowsers.map { it.pluginName },
					"frameworks" to ext.karmaFrameworks.map { it.pluginName },
					"reporters" to ext.karmaReporters.map { it.pluginName }.plus("progress"),
					"single-run" to true,
					"no-auto-watch" to true
				)
				properties.putAll(ext.karmaProperties)
				
				karmaFile.writeText(
					"module.exports = function(config) { config.set(${JsonBuilder(properties).toPrettyString()}) };"
				)
			}
		}
		
		project.task<NpmTask>(TASK_INSTALL_NPM_DEPENDENCIES) {
			val dependencies = listOf(NPM_KARMA)
				.plus(ext.karmaFrameworks.flatMap(KarmaPlugin::pluginDependencies))
				.plus(ext.karmaBrowsers.flatMap(KarmaPlugin::pluginDependencies))
				.plus(ext.karmaReporters.flatMap(KarmaPlugin::pluginDependencies))
				.plus(ext.nodeDependencies)
			
			inputs.property("dependencies", dependencies.joinToString { it.toString() })
			outputs.dir(nodeModulesDir)
			
			setArgs(listOf("install", "--silent") + dependencies.map { it.toString() })
			
			setExecOverrides(closureOf<ExecSpec> {
				standardOutput = object : OutputStream() {
					override fun write(b: Int) {}
				}
			})
		}
		
		project.task<NodeTask>(TASK_RUN) {
			dependsOn(TASK_CREATE_KARMA_CONFIG, TASK_INSTALL_NPM_DEPENDENCIES)
			
			setScript(nodeModulesDir.resolve("karma/bin/karma"))
			setArgs(listOf("start", karmaFile.absolutePath))
			
		}
		
		project.tasks["test"].dependsOn(TASK_RUN)
	}
	
}