package com.dzaitsev.flowexample

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~

 * @author Dmitriy Zaitsev
 * *
 * @since 2015-Nov-29, 12:58
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Layout(val value: Int)
