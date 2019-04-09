package ru.capjack.gradle.kojste

import org.jetbrains.kotlin.gradle.dsl.KotlinSingleJavaTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

val KotlinSingleJavaTargetExtension.target: KotlinTarget
	get() {
		@Suppress("UNCHECKED_CAST")
		val p: KProperty1<KotlinSingleJavaTargetExtension, KotlinTarget> =
			this::class.declaredMemberProperties.first { it.name == "target" } as KProperty1<KotlinSingleJavaTargetExtension, KotlinTarget>
		return p.get(this)
	}
	