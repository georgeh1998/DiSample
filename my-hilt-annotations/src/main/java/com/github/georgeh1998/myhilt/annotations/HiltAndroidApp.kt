package com.github.georgeh1998.myhilt.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class HiltAndroidApp(val modules: Array<KClass<*>> = [])
