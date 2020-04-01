package com.atlassian.bootgraph.api

import com.atlassian.bootgraph.FakeApplicationContext
import com.atlassian.bootgraph.graphviz.ExportConfiguration
import com.atlassian.bootgraph.api.filter.InputPathFilter
import com.atlassian.bootgraph.api.mapper.InputNodeMapper
import com.atlassian.bootgraph.api.matcher.InputNodeMatcher
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class BootGraphTests {

    @Test
    fun buildWithAllParameters() {
        BootGraph.Builder()
                .applicationName("my app")
                .applicationContext(FakeApplicationContext())
                .exportConfig(ExportConfiguration())
                .filter(InputPathFilter("A"))
                .mapper(InputNodeMatcher(), InputNodeMapper())
                .build()
    }

    @Test
    fun withoutApplicationName() {
        assertThrows(IllegalArgumentException::class.java) {
            BootGraph.Builder()
                    .applicationContext(FakeApplicationContext())
                    .exportConfig(ExportConfiguration())
                    .filter(InputPathFilter("A"))
                    .mapper(InputNodeMatcher(), InputNodeMapper())
                    .build()
        }
    }

    @Test
    fun withoutApplicationContext() {
        assertThrows(IllegalArgumentException::class.java) {
            BootGraph.Builder()
                    .applicationName("app")
                    .exportConfig(ExportConfiguration())
                    .filter(InputPathFilter("A"))
                    .mapper(InputNodeMatcher(), InputNodeMapper())
                    .build()
        }
    }

    @Test
    fun withoutExportConfig() {
        val config = BootGraph.Builder()
                .applicationName("app")
                .applicationContext(FakeApplicationContext())
                .filter(InputPathFilter("A"))
                .mapper(InputNodeMatcher(), InputNodeMapper())
                .build()

        assertNotNull(config.exportConfig)
    }


}