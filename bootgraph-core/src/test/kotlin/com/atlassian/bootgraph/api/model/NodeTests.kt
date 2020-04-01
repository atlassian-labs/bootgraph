package com.atlassian.bootgraph.api.model

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class NodeTests {

    @Test
    fun outputConnectionIsAddedToBothSides() {
        val a = ExternalNode("A")
        val b = InternalNode("B")
        a.addEdgeTo(b, "foo")
        assertEquals(1, a.outputs.size)
        assertEquals(1, b.inputs.size)
    }

    @Test
    fun inputConnectionIsAddedToBothSides() {
        val a = ExternalNode("A")
        val b = InternalNode("B")
        a.addEdgeFrom(b, "foo")
        assertEquals(1, a.inputs.size)
        assertEquals(1, b.outputs.size)
    }
}
