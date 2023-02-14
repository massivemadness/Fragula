package com.fragula2.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalBaselineProfilesApi::class)
@RunWith(AndroidJUnit4ClassRunner::class)
class BaselineProfileBenchmark {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateXmlProfile() = generate(AppVariant.XML)

    @Test
    fun generateComposeProfile() = generate(AppVariant.COMPOSE)

    private fun generate(appVariant: AppVariant) {
        baselineProfileRule.collectBaselineProfile(
            packageName = "com.fragula2.sample",
        ) {
            pressHome()
            startActivityAndWait(appVariant)
        }
    }
}