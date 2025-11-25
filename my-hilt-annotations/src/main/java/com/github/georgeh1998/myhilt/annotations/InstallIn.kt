package com.github.georgeh1998.myhilt.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InstallIn(val value: KClass<*>)

object SingletonComponent
