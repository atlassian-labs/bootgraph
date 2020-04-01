package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.ExternalNode
import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.InternalNode
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail
import org.junit.jupiter.api.Test

internal class OutputPathFilterTests {

    @Test
    fun filtersOutputPath() {
        val model = applicationModelWithPathFromAToD()
        val filteredModel = OutputPathFilter("D").applyTo(model)

        assertTrue(filteredModel.containsNode("A"))
        assertTrue(filteredModel.containsNode("B"))
        assertTrue(filteredModel.containsNode("C"))
        assertTrue(filteredModel.containsNode("D"))
        assertNotNull(filteredModel.getEdge("A", "B"))
        assertNotNull(filteredModel.getEdge("B", "C"))
        assertNotNull(filteredModel.getEdge("C", "D"))

        assertFalse(filteredModel.containsNode("E"))
        assertFalse(filteredModel.containsNode("X"))
        assertFalse(filteredModel.containsNode("Y"))
        assertFalse(filteredModel.containsNode("Z"))
    }

    @Test
    fun breaksOnCycle() {
        val model = applicationModelWithCycle()
        val filteredModel = OutputPathFilter("D").applyTo(model)

        assertTrue(filteredModel.containsNode("A"))
        assertTrue(filteredModel.containsNode("B"))
        assertTrue(filteredModel.containsNode("C"))
        assertTrue(filteredModel.containsNode("D"))
        assertNotNull(filteredModel.getEdge("A", "B"))
        assertNotNull(filteredModel.getEdge("B", "C"))
        assertNotNull(filteredModel.getEdge("C", "D"))

        assertFalse(filteredModel.containsNode("E"))
        assertFalse(filteredModel.containsNode("X"))
        assertFalse(filteredModel.containsNode("Y"))
        assertFalse(filteredModel.containsNode("Z"))
    }

    private fun applicationModelWithPathFromAToD(): GraphModel {

        // nodes in the path
        val a = ExternalNode("A")
        val b = InternalNode("B")
        val c = InternalNode("C")
        val d = InternalNode("D")

        a.addEdgeTo(b)
        b.addEdgeTo(c)
        c.addEdgeTo(d)

        // nodes not in the path
        val e = ExternalNode("E")
        val x = ExternalNode("X")
        val y = InternalNode("Y")
        val z = InternalNode("Z")
        c.addEdgeTo(e)
        x.addEdgeTo(y)
        y.addEdgeTo(z)

        val model = GraphModel("application")
        model.addNodes(a, b, c, d, e, x, y, z)
        return model
    }

    private fun applicationModelWithCycle(): GraphModel {
        val model = applicationModelWithPathFromAToD()
        val a = model.getNode("A") ?: fail("expected graph to contain node 'A' ")
        val c = model.getNode("C") ?: fail("expected graph to contain node 'C' ")

        c.addEdgeTo(a)

        return model
    }
}
