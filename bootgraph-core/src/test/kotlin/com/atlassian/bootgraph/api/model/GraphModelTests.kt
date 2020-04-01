package com.atlassian.bootgraph.api.model

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class GraphModelTests {

    companion object{
        val logger = LoggerFactory.getLogger(GraphModelTests.javaClass)
    }

    @Test
    fun filterInternalNodes() {
        val graph = GraphModel("my app")
        graph.addNode(InternalNode("internal"))

        assertEquals(1, graph.getInternalNodes().size)
        assertEquals(0, graph.getExternalNodes().size)
    }

    @Test
    fun filterExternalNodes() {
        val graph = GraphModel("my app")
        graph.addNode(ExternalNode("external"))

        assertEquals(0, graph.getInternalNodes().size)
        assertEquals(1, graph.getExternalNodes().size)
    }

    @Test
    fun testToDebugString() {
        val a = ExternalNode("A")
        val b = InternalNode("B")
        val c = ExternalNode("C")
        val d = ExternalNode("D")
        a.addEdgeTo(b, "A to B")
        b.addEdgeTo(c, "B to C")

        logger.info(GraphModel("my app")
                .addNodes(a, b, c, d)
                .toDebugString())
    }
}
