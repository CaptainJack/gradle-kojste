package ru.capjack.gradle.ktjs.test

object Config {
	const val EXTENSION = "ktjsTest"
	const val BUILD_DIR = "test-js"
	
	const val TASK_RUN = "ktjsTestRun"
	const val TASK_COPY_DEPENDENCIES = "ktjsTestCopyDependencies"
	const val TASK_CREATE_KARMA_CONFIG = "ktjsTestInitKarmaConfig"
	const val TASK_INSTALL_NPM_DEPENDENCIES = "ktjsTestInitNpmDependencies"
	
	const val VERSION_NODE = "8.11.4"
	
	val NPM_KARMA = NpmPackage("karma", "3.0.0")
	
	val NPM_KARMA_CHROME_LAUNCHER = NpmPackage("karma-chrome-launcher", "2.2.0")
	val NPM_KARMA_PHANTOMJS_LAUNCHER = NpmPackage("karma-phantomjs-launcher", "1.0.4")
	val NPM_PHANTOMJS = NpmPackage("phantomjs-prebuilt", "2.1.16")
	val NPM_KARMA_FIREFOX_LAUNCHER = NpmPackage("karma-firefox-launcher", "1.1.0")
	val NPM_KARMA_OPERA_LAUNCHER = NpmPackage("karma-opera-launcher", "1.0.0")
	val NPM_KARMA_IE_LAUNCHER = NpmPackage("karma-ie-launcher", "1.0.0")
	val NPM_KARMA_SAFARI_LAUNCHER = NpmPackage("karma-safari-launcher", "1.0.0")
	
	val NPM_KARMA_JASMINE = NpmPackage("karma-jasmine", "1.1.2")
	val NPM_JASMINE_CORE = NpmPackage("jasmine-core", "3.2.1")
	val NPM_KARMA_MOCHA = NpmPackage("karma-mocha", "1.3.0")
	val NPM_MOCHA = NpmPackage("mocha", "5.2.0")
	val NPM_KARMA_QUNIT = NpmPackage("karma-qunit", "2.1.0")
	val NPM_QUNIT = NpmPackage("qunit", "2.6.2")
	
	val NPM_KARMA_GROWL_REPORTER = NpmPackage("karma-growl-reporter", "1.0.0")
	val NPM_KARMA_JUNIT_REPORTER = NpmPackage("karma-junit-reporter", "1.2.0")
	val NPM_KARMA_TEAMCITY_REPORTER = NpmPackage("karma-teamcity-reporter", "1.1.0")
	val NPM_KARMA_COVERAGE = NpmPackage("karma-coverage", "1.1.2")
}