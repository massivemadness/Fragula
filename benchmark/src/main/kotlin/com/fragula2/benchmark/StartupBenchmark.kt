package com.fragula2.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupXmlCompilationNone() = startup(AppVariant.XML, CompilationMode.None())

    @Test
    fun startupXmlCompilationPartial() = startup(AppVariant.XML, CompilationMode.Partial())

    @Test
    fun startupComposeCompilationNone() = startup(AppVariant.COMPOSE, CompilationMode.None())

    @Test
    fun startupComposeCompilationPartial() = startup(AppVariant.COMPOSE, CompilationMode.Partial())

    private fun startup(appVariant: AppVariant, compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = "com.fragula2.sample",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 5,
        ) {
            pressHome()
            startActivityAndWait(appVariant)
        }
    }
}